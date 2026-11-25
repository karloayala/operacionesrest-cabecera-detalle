ALTER TABLE ventas
    DROP CONSTRAINT IF EXISTS ventas_estado_check;

ALTER TABLE ventas
    ADD CONSTRAINT ventas_estado_check
    CHECK (estado IN (
        'PENDIENTE',
        'CONFIRMADA',
        'PAGADA',
        'PAGADO_AL_CONTADO',
        'CANCELADA',
        'COMPLETADA',
        'PAGANDO'
    ));
