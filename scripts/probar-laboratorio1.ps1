$ErrorActionPreference = 'Stop'

$baseUrl = 'http://localhost:8080'
$runId = [guid]::NewGuid().ToString('N').Substring(0, 8)

function Send-JsonRequest {
    param(
        [Parameter(Mandatory)] [string] $Method,
        [Parameter(Mandatory)] [string] $Path,
        [hashtable] $Body
    )

    $parameters = @{
        Method = $Method
        Uri = "$baseUrl$Path"
    }
    if ($Body) {
        $parameters.ContentType = 'application/json'
        $parameters.Body = $Body | ConvertTo-Json -Depth 5
    }
    Invoke-RestMethod @parameters
}

function Get-OrCreateEtiqueta {
    param([Parameter(Mandatory)] [string] $Nombre)

    $existente = (Invoke-RestMethod -Uri "$baseUrl/api/etiquetas" |
        Where-Object { $_.nombre -eq $Nombre } | Select-Object -First 1)
    if ($existente) {
        return $existente
    }
    return Send-JsonRequest POST '/api/etiquetas' @{ nombre = $Nombre }
}

$categorias = Invoke-RestMethod -Uri "$baseUrl/api/categorias"
$categoria = $categorias | Where-Object { $_.nombre -eq 'Computadoras' } | Select-Object -First 1
if (-not $categoria) {
    $categoria = Send-JsonRequest POST '/api/categorias' @{ nombre = 'Computadoras'; activa = $true }
}

$proveedor = Send-JsonRequest POST '/api/proveedores' @{
    nombre = 'Proveedor Laboratorio'
    telefono = '2255-0303'
    correo = "lab-$runId@example.com"
    activo = $true
}

$etiquetas = @('Oferta', 'Importado', 'Empresarial', 'Portátil', 'Gaming')
foreach ($nombreEtiqueta in $etiquetas) {
    $null = Get-OrCreateEtiqueta $nombreEtiqueta
}
$gaming = Get-OrCreateEtiqueta 'Gaming'

$laptop = Send-JsonRequest POST '/api/productos' @{
    codigo = "LAB-LAP-$runId"
    nombre = 'Laptop Lenovo'
    descripcion = 'Equipo del Laboratorio Uno'
    precioVenta = 850.00
    existencia = 10
    categoriaId = $categoria.id
    proveedorId = $proveedor.id
}
$mouse = Send-JsonRequest POST '/api/productos' @{
    codigo = "LAB-MOUSE-$runId"
    nombre = 'Mouse inalambrico'
    descripcion = 'Accesorio del Laboratorio Uno'
    precioVenta = 18.50
    existencia = 25
    categoriaId = $categoria.id
    proveedorId = $proveedor.id
}

$null = Send-JsonRequest POST "/api/productos/$($laptop.id)/etiquetas/$($gaming.id)" $null
$null = Send-JsonRequest POST "/api/productos/$($mouse.id)/etiquetas/$($gaming.id)" $null
$actualizado = Send-JsonRequest PUT "/api/productos/$($laptop.id)" @{
    codigo = $laptop.codigo
    nombre = 'Laptop Lenovo actualizada'
    descripcion = 'Producto actualizado por el smoke test'
    precioVenta = 899.99
    existencia = 12
    categoriaId = $categoria.id
    proveedorId = $proveedor.id
}

$porEtiqueta = Invoke-RestMethod -Uri "$baseUrl/api/productos/etiqueta/$($gaming.id)"
if ($porEtiqueta.id -notcontains $laptop.id -or $porEtiqueta.id -notcontains $mouse.id) {
    throw 'El filtro por etiqueta no devolvio los dos productos asociados.'
}

$null = Send-JsonRequest DELETE "/api/productos/$($laptop.id)/etiquetas/$($gaming.id)" $null
$laptopSinEtiqueta = Invoke-RestMethod -Uri "$baseUrl/api/productos/$($laptop.id)"
$etiquetasPersistentes = Invoke-RestMethod -Uri "$baseUrl/api/etiquetas"
if ($laptopSinEtiqueta.etiquetas -contains 'Gaming' -or
    -not ($etiquetasPersistentes.nombre -contains 'Gaming')) {
    throw 'Eliminar la asociacion afecto el producto o elimino la etiqueta.'
}

$null = Send-JsonRequest DELETE "/api/productos/$($mouse.id)" $null
try {
    Invoke-RestMethod -Uri "$baseUrl/api/productos/$($mouse.id)" | Out-Null
    throw 'El producto eliminado todavia puede consultarse.'
} catch {
    if ($_.Exception.Response.StatusCode.value__ -ne 404) {
        throw
    }
}

[pscustomobject]@{
    estado = 'Correcto'
    migraciones = 4
    etiquetasRequeridas = $etiquetas
    laptopActualizada = $actualizado.nombre
    productosPorEtiquetaIniciales = $porEtiqueta.Count
    productoAlternoEliminado = $mouse.id
    etiquetaGamingConservada = [bool]($etiquetasPersistentes.nombre -contains 'Gaming')
} | ConvertTo-Json -Depth 5
