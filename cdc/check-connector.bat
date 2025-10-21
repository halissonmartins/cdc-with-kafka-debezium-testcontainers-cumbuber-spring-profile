SET HTTP=
for /f %%a in ( 'curl -X GET -H "Content-Type:application/json" http://localhost:8083/connectors' ) do set HTTP=%%a

echo %HTTP%

if "%HTTP%" == "200" (
    exit /b 0
) else (
    exit /b 1
)