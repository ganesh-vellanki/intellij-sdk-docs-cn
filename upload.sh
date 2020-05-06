#!/usr/bin/env bash

$REPO=./site/intellij/sdk/docs/

rake build

rm -r ./site/

mkdir -p $REPO
cp ./_site/* $REPO/

cd site
git init
git remote add origin git@github.com:intellij-sdk-docs-cn/intellij-sdk-docs-cn.github.io.git
git add .
git commit -m "Upload"
git push origin master -uf
