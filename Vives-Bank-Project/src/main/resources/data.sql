-- Inserción de Usuarios
INSERT INTO usuarios (guid, username, password, created_at, updated_at, is_deleted)
VALUES
    ('1111aaaa', 'userName', '$2a$10$to0IqpINy9GXDo4IH9SKIOOT0cU5kg692jLdV0aPzR/rF3cUt97Fy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
    ('2222bbbb', 'adminName', '$2a$10$to0IqpINy9GXDo4IH9SKIOOT0cU5kg692jLdV0aPzR/rF3cUt97Fy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

INSERT INTO user_roles (user_id, roles)
VALUES
    (1, 'USER'),
    (2, 'USER'),
    (2, 'ADMIN');

-- Inserción de TipoCuentas
INSERT INTO tipo_Cuenta (guid, nombre, interes, created_at, updated_at, is_deleted)
VALUES
    ('3333cccc', 'Cuenta Corriente', 1.5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
    ('4444dddd', 'Cuenta Ahorro', 2.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

-- Inserción de Tarjetas
INSERT INTO tarjetas (guid, numero_tarjeta, fecha_caducidad, cvv, pin, limite_diario, limite_semanal, limite_mensual, tipo_tarjeta, created_at, updated_at, is_deleted)
VALUES
    ('5555eeee', '4149434231419594', '2034-12-31', 123, '1234', 1000.00, 5000.00, 20000.00, 'DEBITO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
    ('6666ffff', '4302320784410830', '2034-12-31', 234, '5678', 1500.00, 6000.00, 25000.00, 'CREDITO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

-- Inserción de Clientes
INSERT INTO clientes (guid, dni, nombre, apellidos, calle, numero, codigo_postal, piso, letra, email, telefono, foto_perfil, foto_dni, created_at, updated_at, is_deleted, user_id)
VALUES
    ('7777gggg', '12345678A', 'Juan', 'Perez', 'Calle Test', '100', '28014', '1', 'A', 'juan.perez@email.com', '612345678', 'foto_perfil_juan.jpg', 'foto_dni_juan.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 1),
    ('8888hhhh', '87654321B', 'Maria', 'Lopez', 'Calle Test', '200', '28014', '2', 'B', 'maria.lopez@email.com', '698765432', 'foto_perfil_maria.jpg', 'foto_dni_maria.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 2);

-- Inserción de Cuentas
INSERT INTO cuentas (guid, iban, saldo, tipo_Cuenta_id, tarjeta_id, cliente_id, created_at, updated_at, is_deleted)
VALUES
    ('9999iiii', 'ES64123412344820495463', 1000.00, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
    ('0000jjjj', 'ES60123412347246753334', 2000.00, 2, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);