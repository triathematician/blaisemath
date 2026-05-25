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
    Write-Host "`n==> Clean-installing $module" -ForegroundColor Cyan
    Push-Location "$root\$module"
    mvn clean install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "FAILED: $module" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    Pop-Location
}

Write-Host "`nAll modules installed successfully." -ForegroundColor Green
