# (1).环境需求：
    1.DockerDesktop
    2.Maven
    3.Java（JDK17）
    4.Nodejs
    5.HBuilderX
# (2).如何跑起来：（docker-compose一键生产部署）
    1. 打包Server
        --【cd Server】
        --【mvn install】
        --【mvn clean package -DskipTests】
    2. 打包Web
        --【cd Web】
        --【npm install -g pnpm】
        --【pnpm install】
        --【npm run build:prod】
    3. 打包MallWeb
        --【cd MallFrontend】
        --【pnpm install】
        -- 进入 HBuilderX
        --【发行】->【网站-PC Web或手机H5】，选项都不用勾选，设置随便
        -- 可能会有appid的报错，自己去查，很简单
    4. 打包InitService
        --【cd InitService】
        --【mvn clean package】
    5. 导入docker镜像：
        --【cd docker-images】
        --【docker load -i eclipse-temurin-17-jdk.tar】
        --【docker load -i mysql-8.0.tar】
        --【docker load -i redis-6-alpine.tar】
        --【docker load -i rabbitmq-3-management-alpine.tar】
        --【docker load -i tdengine-3.3.6.0.tar】
        --【docker load -i nginx-stable-alpine.tar】
        --【docker load -i mitedtsm-init-service-latest.tar】
        --【docker load -i mitedtsm-server-latest.tar】
    6. 运行docker-compose
        --【cd docker-compose】
        --【docker-compose up -d --build】
# (3).如何用本机跑起来:(本地操作和调试)
    1. 按照上面的，先把所有环境装好
    2. 启动开发环境（只启动基础环境，不包含前后端）
        --【cd dev】
        --【docker-compose up -d】
        -- 如果本地已经有部分环境（如 MySQL），可以按需启动，只启没有的：
            docker-compose up -d redis rabbitmq tdengine init-service     （跳过 MySQL）
            docker-compose up -d rabbitmq tdengine init-service           （跳过 MySQL 和 Redis）
            docker-compose up -d tdengine init-service                    （只启动 TDengine + 初始化）
            ……以此类推，缺什么启什么
        -- ⚠ 注意：只要启动了 tdengine，就必须带上 init-service
           init-service 负责在 TDengine 中创建初始数据库，执行完会自动退出
    3. 本地运行 Server
        -- 找到【Server】->【mitedtsm-server】->【src】-> …… ->【MitedtsmServerApplication】
        -- 然后点击IDEA右上角的小绿箭头
    4. 本地运行 Web
        --【cd Web】
        --【npm run dev】
    5. 本地运行 mall
        -- 进入 HBuilderX
        --【运行】->【运行到浏览器】
# (4).我拿到了新版本，该怎么覆盖运行
    1.【cd docker-compose】
    2.【docker-compose down -v】          // 删除所有数据卷m r
    3. 然后就回到第2步或者第3步
# (5).项目说明:
    1. 整个项目的逻辑是:其他全是可挂载和拆卸的模块,真正的后端是 mitedtsm-server ,启动类也在这里
    2. 整个项目的 application 配置文件放在 mitedtsm-server 下面,其中的 application-local 是所有的连接配置
    3. Web 和 MallFrontend 都要用 pnpm install,MallFrontend 的环境文件是那个.env
    4. 所有的相关文档全部放在 项目根目录的 docs 下面了。项目根目录下的【EDIT_GUIDE_BY_3031.md】是详细的 修改指引文档
    5. 你docker-compose一键部署之后,你会发现依旧一部分是中文的
        -- 因为字典数据是 业务数据,是不会进行国际化的
        -- 如果需要的话,请手动 在你的数据库中 执行【database/replace】下面的所有文件
    6. 开发环境配置在【dev】目录下,只启动基础环境(MySQL、Redis、RabbitMQ、TDengine)
        -- 适合本地开发和调试，不需要每次都手动关闭前后端容器
        -- 本地已有部分环境时，按需启动：docker-compose up -d 服务名1 服务名2 ...
# (6).默认环境：（开发环境）
    1. MySQL    // localhost，3306，root，1234
    2. redis    // localhost，6379，无密码
    3. TDengine // localhost，6041
    4. RabbitMQ // localhost，剩下忘了，反正是默认的，账号密码都是rabbit
# (7).打包到 docker-compose 有哪些东西
    1.【MySQL】
    2.【TDengine】
    3.【Redis】
    4.【RabbitMQ】
    5. Server后端
    6. Web前端
    7. Mall的Web前端
    8. 初始化容器（负责在TDengine里面创建初始数据库，执行完就会停）
# (8). 关于 Scripts 下面的脚本
    1.【remove_bom.py】       // 批量移除UTF-8文件开头的BOM头的
    2.【test.py】             // 检测指定乱码的
    3.【scan_chinese】        // 扫描中文硬编码的（做国际化）
# (9). 关于 database 下面的 SQL 脚本
    1.【base】                // 系统的基本数据，先ruoyi-vue-pro，然后是quartz，剩余的随便
    2.【new】                 // 在原有系统上，追加或修改的数据
    3.【replace-en】          // 手动将上面2项的部分数据替换为英语（docker-compose不会自动执行）