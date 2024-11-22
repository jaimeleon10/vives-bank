INSERT INTO public.usuarios (id, guid, username, password, created_at, updated_at, is_deleted)
VALUES
    (
        DEFAULT,
        'GUID_GENERADO',
        'admin',
        'admin',
        '2024-11-21 09:55:47.903101',
        '2024-11-21 09:55:47.903101',
        false
    );

INSERT INTO public.user_roles (user_id, role)
VALUES ((SELECT id FROM public.usuarios WHERE guid = 'GUID_GENERADO'), 'ADMIN');

INSERT INTO public.clientes
(
    guid,
    created_at,
    updated_at,
    apellidos,
    dni,
    email,
    foto_dni,
    foto_perfil,
    id,
    nombre,
    telefono,
    user_id,
    id_movimientos,
    is_deleted
)
VALUES
    (
        'GUID_GENERADO',
        '2024-11-21 09:56:33.913479',
        '2024-11-21 12:11:38.065944',
        'Herrero',
        '50378911X',
        'alvaro@gmail.com',
        'alvaro.jpg',
        'alvaro.png',
        DEFAULT,
        'alvaro',
        '656537860',
        (SELECT id FROM public.usuarios WHERE guid = 'GUID_GENERADO'),
        E'\\xACED00057372002A6F72672E62736F6E2E74797065732E4F626A65637449642453657269616C697A6174696F6E50726F787900000000000000010200015B000562797465737400025B427870757200025B42ACF317F8060854E002000078700000000C673F1569034E2E357248C445',
        false
    );


INSERT INTO public.tipo_cuenta (interes, created_at, updated_at, id, nombre, is_deleted)
VALUES
    (
        2.00,
        '2024-11-21 12:00:38.509861',
        '2024-11-21 12:00:38.509861',
        DEFAULT,
        'testTCuenta',
        FALSE
    );

INSERT INTO public.tipo_tarjetas (nombre, created_at, updated_at, id) VALUES (1, '2024-11-21 12:02:33.602339', '2024-11-21 12:02:33.602339', 'e7dd8cf6-83dd-41cf-8a4b-bb12b5aca4f5');

INSERT INTO public.tarjetas
(cvv, fecha_caducidad, limite_diario, limite_mensual, limite_semanal, created_at, updated_at, id, tipo_tarjeta_id, numero_tarjeta, pin, is_deleted)
VALUES
    (
        111,
        '2034-11-30',
        1000.00,
        1000.00,
        1000.00,
        '2024-11-21 12:03:44.141103',
        '2024-11-21 12:03:44.141103',
        DEFAULT,
        'e7dd8cf6-83dd-41cf-8a4b-bb12b5aca4f5',
        '1111111111111111',
        '123',
        FALSE
    );

INSERT INTO public.cuentas
(is_deleted, saldo, created_at, updated_at, tarjeta_id, cliente_id, iban, id, tipo_cuenta_id)
VALUES
    (
        false,
        10000.00,
        '2024-11-21 12:07:28.734354',
        '2024-11-21 12:07:28.734354',
        'e7dd8cf6-83dd-41cf-8a4b-bb12b5aca4f4',
        'asd',
        'ES9121000418450200051332',
        DEFAULT,
        'tcuenta'
    );
