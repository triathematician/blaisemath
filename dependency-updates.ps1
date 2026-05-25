$modules = @(
    "blaise-common",
    "blaise-graphics",
    "blaise-graph-theory",
    "blaise-json",
    "blaise-graphics-ui",
    "blaise-graph-theory-ui",
    "blaise-svg"
)

$root = $PSScriptRoot

foreach ($module in $modules) {
    Write-Host "`n==> Dependency updates for $module" -ForegroundColor Cyan
    Push-Location "$root\$module"
    mvn versions:display-dependency-updates
    Pop-Location
}
