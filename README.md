![GitHub contributors](https://img.shields.io/github/contributors/WeiYe-Jing/datax-web)
![GitHub issues](https://img.shields.io/github/issues/WeiYe-Jing/datax-web)
![GitHub](https://img.shields.io/github/license/WeiYe-Jing/datax-web)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/WeiYe-Jing/datax-web)
![](https://img.shields.io/badge/qq%E7%BE%A4-776939467-green.svg)

# DataX-Web

[![Stargazers over time](https://starchart.cc/WeiYe-Jing/datax-web.svg)](https://starchart.cc/WeiYe-Jing/datax-web)

DataX Web是在DataX之上开发的分布式数据同步工具，提供简单易用的操作界面，降低用户使用DataX的学习成本，缩短任务配置时间，避免配置过程中出错。用户可通过页面选择数据源即可创建数据同步任务，支持RDBMS、Hive、HBase、ClickHouse、MongoDB等数据源，RDBMS数据源可批量创建数据同步任务，支持实时查看数据同步进度及日志并提供终止同步功能，集成并二次开发xxl-job可根据时间、自增主键增量同步数据。

任务"执行器"支持集群部署，支持执行器多节点路由策略选择，支持超时控制、失败重试、失败告警、任务依赖，执行器CPU、内存、负载的监控等等。

## Architecture

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/DataX-Web.png)

## System Requirements

- Java 8 (1.8.201+)
- Python 2.7 (支持Python3需要替换 `datax/bin` 下的三个python文件，替换文件在 `doc/datax-web/datax-python3` 下)
- PostgreSQL (默认配置) 或 MySQL
- MacOS / Windows / Linux

## Quick Start

- [Quick Start](https://github.com/WeiYe-Jing/datax-web/blob/master/userGuid.md)
- [Linux 一键部署](https://github.com/WeiYe-Jing/datax-web/blob/master/doc/datax-web/datax-web-deploy.md)

## Build

```bash
# 编译 admin 和 executor
mvn package -pl datax-admin,datax-executor -am -DskipTests

# 编译全部模块
mvn package -DskipTests

# Docker
docker build -t wuhanchu/datax-web:2.1.2 .
docker push wuhanchu/datax-web:2.1.2
```

产物位置：
- `datax-admin/target/datax-admin-2.1.2.jar`
- `datax-executor/target/datax-executor-2.1.2.jar`

## Modules

| 模块 | 说明 |
|---|---|
| `datax-admin` | 管理后台、调度中心 |
| `datax-executor` | 任务执行器，运行DataX进程 |
| `datax-core` | 共享库：任务模型、RPC客户端、工具类 |
| `datax-rpc` | Netty RPC框架（基于xxl-job） |
| `datax-assembly` | 打包模块 |

## Configuration

### Admin

环境变量：

| 变量 | 说明 | 默认值 |
|---|---|---|
| `PORT` | 服务端口 | - |
| `DB_HOST` | 数据库地址 | `127.0.0.1` |
| `DB_PORT` | 数据库端口 | `3306` |
| `DB_DATABASE` | 数据库名 | `datax` |
| `DB_USERNAME` | 数据库用户 | - |
| `DB_PASSWORD` | 数据库密码 | - |
| `datax.path` | 日志存储路径 | - |

### Executor

环境变量：

| 变量 | 说明 | 默认值 |
|---|---|---|
| `PORT` | 服务端口 | - |
| `ADDRESSES` | Admin地址 | - |
| `json.path` | DataX JSON临时目录 | - |
| `python.path` | DataX Python脚本路径 | - |
| `datax.executor.maxConcurrent` | 最大并发DataX任务数 | `10` |

### 运行时调整并发

```bash
# 启动 executor
java -jar datax-executor-2.1.2.jar --PORT=9999 --ADDRESSES=http://admin:2020 --datax.executor.maxConcurrent=10

# 查看当前并发限制
curl http://localhost:9999/executor/maxConcurrent

# 动态调整
curl -X POST "http://localhost:9999/executor/maxConcurrent?value=5"
```

## Features

### 数据源与JSON构建

- 支持Hive、MySQL、Oracle、PostgreSQL、SQLServer、HBase、MongoDB、ClickHouse数据源JSON构建
- RDBMS数据源批量创建任务
- 支持preSql、postSql配置
- 数据源用户名密码加密存储

### 任务管理

- 支持DataX、Shell、Python、PowerShell任务类型
- 定时任务，动态修改任务状态、启动/停止
- 任务超时控制，超时自动kill DataX进程
- 失败重试、失败邮件告警
- 子任务依赖
- 增量同步（时间、主键、分区）
- 任务模板，常用任务快速创建
- 项目管理模块，任务分类管理

### 调度与执行

- 基于xxl-job的中心式调度，支持集群部署
- 执行器自动注册，支持多种路由策略（轮询、随机、故障转移等）
- 阻塞处理策略：单机串行、丢弃后续调度、覆盖之前调度

### 监控与日志

- 实时查看DataX执行日志
- 执行器CPU、内存、负载监控
- 运行报表

## Introduction

### 1. 执行器配置

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/executor.png)

#### 执行器属性说明

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/add_executor.png)

```
1、AppName: 与datax-executor中application.yml的datax.job.executor.appname保持一致
2、名称: 执行器的可读名称
3、排序: 任务新增时按此排序读取可用执行器列表
4、注册方式:
   - 自动注册: 执行器自动注册，调度中心动态发现
   - 手动录入: 人工维护执行器地址，多地址逗号分隔
```

### 2. 创建数据源

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/add_datasource.png)

### 3. 创建任务模板

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/template_list.png)

### 4. 构建JSON脚本

选择数据源，映射字段，生成JSON。

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/build.png)
![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/mapping.png)
![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/select_template.png)

### 5. 批量创建任务

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/batch_build_r.png)
![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/batch_build_w.png)

### 6. 任务创建

支持DataX、Shell、Python、PowerShell任务。

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/datax.png)
![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/shell.png)

#### 阻塞处理策略

- **单机串行**：FIFO队列串行运行
- **丢弃后续调度**：存在运行任务时，新请求丢弃并标记失败
- **覆盖之前调度**：终止运行中的任务，运行新任务

> 增量同步建议设置为丢弃后续调度或单机串行。设置单机串行时注意：失败重试次数 × 每次执行时间 < 调度周期，否则会导致数据重复。

- [增量参数设置](https://github.com/WeiYe-Jing/datax-web/blob/master/doc/datax-web/increment-desc.md)
- [分区参数设置](https://github.com/WeiYe-Jing/datax-web/blob/master/doc/datax-web/partition-dynamic-param.md)

### 7. 任务列表

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/job.png)

### 8. 日志查看

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/log_stat.png)
![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/img/log_detail.png)

### 9. 任务资源监控

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/img/monitor.png)

### 10. 用户管理

![](https://datax-web.oss-cn-hangzhou.aliyuncs.com/doc/user.png)

## UI

[前端github地址](https://github.com/WeiYe-Jing/datax-web-ui)

## Contributing

Contributions are welcome! Open a pull request to fix a bug, or open an Issue to discuss a new feature or change.

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 Issue 讨论新特性或者变更。

## License

MIT License

Copyright (c) 2020 WeiYe

## Changelog

### v-2.1.2

**新增**
- 添加项目管理模块，可对任务分类管理
- RDBMS数据源批量任务创建功能
- JSON构建增加ClickHouse数据源支持
- 执行器CPU、内存、负载监控页面图形化
- RDBMS增量抽取增加主键自增方式
- 更换MongoDB数据源连接方式，重构HBase数据源JSON构建模块
- 脚本类型任务增加停止功能
- rdbms json构建增加postSql，支持构建多个preSql/postSql
- 合并datax-registry模块到datax-rpc中
- 数据源信息加密算法修改及代码优化
- 时间增量同步支持更多时间格式
- 日志页面增加DataX执行结果统计数据

**升级**
- PostgreSql、SQLServer、Oracle数据源JSON构建增加schema name选择
- DataX JSON字段名称与数据源关键词冲突问题优化
- 任务管理页面按钮展示优化
- 日志管理页面增加任务描述信息
- JSON构建前端form表单缓存数据问题修复
- HIVE JSON构建增加头尾选项参数

> ⚠️ 2.1.1版本不建议直接升级，数据源信息加密方式变更会导致之前已加密的数据源解密失败。如需升级请重建数据源和任务。

### v-2.1.1

**新增**
- HBase数据源支持
- MongoDB数据源支持
- 执行器CPU、内存、负载监控页面
- 24类插件DataX JSON配置样例
- 公共字段自动填充（创建时间、创建人、修改时间、修改者）
- Swagger接口Token验证
- 任务超时时间，超时自动kill DataX进程

**升级**
- 数据源管理用户名密码加密
- JSON文件中用户名密码加密，执行时解密
- 页面菜单整理、图标升级、交互优化
- 日志输出优化，减小文件大小
- logback从yml获取日志路径配置

**修复**
- 任务日志过大时查看日志超时问题
