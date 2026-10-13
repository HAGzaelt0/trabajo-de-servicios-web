CREATE TABLE proveedor (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    telefono VARCHAR(30),
    correo VARCHAR(150) UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE producto ADD COLUMN proveedor_id INTEGER;
ALTER TABLE producto ADD CONSTRAINT fk_producto_proveedor
    FOREIGN KEY (proveedor_id) REFERENCES proveedor(id);
CREATE INDEX idx_producto_proveedor_id ON producto(proveedor_id);