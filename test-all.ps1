$modules = @(
    "blaise-common",
    "blaise-graphics",
    "blaise-graph-theory",
    "blaise-json",
    "blaise-graphics-ui",
    "blaise-svg",
    "blaise-graph-theory-ui"
)

$root = $PSScriptRoot

foreach ($module in $modules) {
    Write-Host "`n==> Testing $module" -ForegroundColor Cyan
    Push-Location "$root\$module"
    mvn test
    if ($LASTEXITCODE -ne 0) {
        Write-Host "FAILED: $module" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    Pop-Location
}

Write-Host "`nAll modules tested successfully." -ForegroundColor Green
