$ErrorActionPreference = 'Stop'

$baseUrl = 'http://localhost:8080'
$runId = [guid]::NewGuid().ToString('N').Substring(0, 8)

function Send-JsonRequest {
    param(
        [Parameter(Mandatory)] [string] $Path,
        [Parameter(Mandatory)] [hashtable] $Body
    )

    Invoke-RestMethod -Method Post -Uri "$baseUrl$Path" `
        -ContentType 'application/json' `
        -Body ($Body | ConvertTo-Json -Depth 5)
}

$computadoras = Send-JsonRequest '/api/categorias' @{ nombre = 'Computadoras'; activa = $true }
$accesorios = Send-JsonRequest '/api/categorias' @{ nombre = 'Accesorios'; activa = $true }
$null = Send-JsonRequest '/api/categorias' @{ nombre = 'Monitores'; activa = $true }

$proveedorCentral = Send-JsonRequest '/api/proveedores' @{
    nombre = 'Distribuidora Central'
    telefono = '2255-0101'
    correo = "central-$runId@example.com"
    activo = $true
}
$proveedorNorte = Send-JsonRequest '/api/proveedores' @{
    nombre = 'Tecnologia Norte'
    telefono = '2255-0202'
    correo = "norte-$runId@example.com"
    activo = $true
}

$laptop = Send-JsonRequest '/api/productos' @{
    codigo = "LAP-$runId"
    nombre = 'Laptop Lenovo'
    descripcion = 'Laptop para trabajo y estudio'
    precioVenta = 850.00
    existencia = 10
    categoriaId = $computadoras.id
    proveedorId = $proveedorCentral.id
}
$mouse = Send-JsonRequest '/api/productos' @{
    codigo = "ACC-$runId"
    nombre = 'Mouse inalambrico'
    descripcion = 'Mouse optico USB'
    precioVenta = 18.50
    existencia = 25
    categoriaId = $accesorios.id
    proveedorId = $proveedorNorte.id
}

if ($laptop.categoriaNombre -ne 'Computadoras' -or
    $laptop.proveedorNombre -ne 'Distribuidora Central' -or
    $mouse.categoriaNombre -ne 'Accesorios' -or
    $mouse.proveedorNombre -ne 'Tecnologia Norte') {
    throw 'La API no devolvio las relaciones esperadas para los productos.'
}

$result = [pscustomobject]@{
    estado = 'Correcto'
    categorias = (Invoke-RestMethod -Uri "$baseUrl/api/categorias").Count
    proveedores = (Invoke-RestMethod -Uri "$baseUrl/api/proveedores").Count
    productos = (Invoke-RestMethod -Uri "$baseUrl/api/productos").Count
    laptop = $laptop
    mouse = $mouse
}

$result | ConvertTo-Json -Depth 5