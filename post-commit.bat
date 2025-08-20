@echo off
REM 检查 git-lfs 是否已安装
where git-lfs >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo This repository is configured for Git LFS but 'git-lfs' was not found on your path.
    echo If you no longer wish to use Git LFS, remove this hook by deleting the 'post-commit' file in the hooks directory (set by 'core.hookspath'; usually '.git/hooks').
    exit /b 2
)

REM 运行 git lfs post-commit
git lfs post-commit %*

REM 自动运行 git lfs prune
echo Running git lfs prune to clean up old LFS files...
git lfs prune