PROGRAM TesteErros;

VAR
    idade, resultado : INTEGER;
    preco : REAL;

BEGIN

    idade := 20

    resultado := 10;

    preco := 15.50;

    IF idade >= 18
        resultado := 1;

    WHILE idade > 0
        idade := idade - 1;

END.

comando pra fazer funcionar:

./mvnw spring-boot:run