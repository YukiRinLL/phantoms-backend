# phantoms-backend
_phantoms Java backend_

## 系统架构图:
![Phantoms.jpg](data/Phantoms.jpg)

## 开发备注:
PS:由于Github的lfs存储空间有限，所以每次提交代码不保留旧版本文件
### 自动清理本地 LFS 文件

需要配置本地项目路径下的.git/hooks/post-commit文件,手动添加下列代码:

```
# 自动运行 git lfs prune
echo "Running git lfs prune to clean up old LFS files..."
git lfs prune
```

也可以执行根目录下的setup-hooks脚本来还原备份文件

备份文件:[post-commit](post-commit)

setup-hooks.sh:
```Bash
#!/bin/bash

# 检查是否在 Git 仓库中
if [ ! -d ".git" ]; then
  echo "This script must be run in a Git repository."
  exit 1
fi
# 复制 post-commit 钩子
cp post-commit .git/hooks/post-commit
chmod +x .git/hooks/post-commit

echo "Git hooks have been set up successfully."
```

手动执行`git lfs prune`命令也可以清理旧的LFS文件。

### 手动清理远程仓库中的 LFS 文件
GitHub 提供了一个工具 git lfs migrate，可以帮助清理远程仓库中的旧版本 LFS 文件。

#### 1.安装 git-lfs 工具
`git lfs install`

#### 2.使用 git lfs migrate 清理远程仓库

git lfs migrate 可以帮助你将旧版本的 LFS 文件从远程仓库中移除：

`git lfs migrate import --include="*.jar" --include-ref=refs/heads/main`

这个命令会将所有 .jar 文件的旧版本从远程仓库中移除，并只保留最新的版本。

#### 3.推送更改到远程仓库
运行以下命令将更改推送到远程仓库：

`git push origin main --force`

#### 4.自动化脚本
```Bash
#!/bin/bash

# 拉取最新更改
git pull origin main
# 运行 git lfs migrate 命令
git lfs migrate import --include="*.jar" --include-ref=refs/heads/main
# 推送更改到远程仓库
git push origin main --force
```

