package cn.tml.dbtool.dbdict;

import java.io.File;
import java.util.ArrayList;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.execute.ExcelFileExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import cn.tml.dbtool.dbdict.config.DatasourceConfig;
import cn.tml.dbtool.dbdict.config.DicFileConfig;
import cn.tml.dbtool.dbdict.constant.AppConstant;
import cn.tml.dbtool.dbdict.util.PropertiesUtil;

public class DbDicApp {
    public static void main(String[] args) {
        DbDicApp tool = new DbDicApp();
        String drCls = PropertiesUtil.getString(AppConstant.CFG_DB_DRCLS);
        String dbUrl = PropertiesUtil.getString(AppConstant.CFG_DB_URL);
        String user = PropertiesUtil.getString(AppConstant.CFG_DB_NAME);
        String pwd = PropertiesUtil.getString(AppConstant.CFG_DB_ACESS);
        DatasourceConfig datasourceConfig = new DatasourceConfig();
        datasourceConfig.setDbUrl(dbUrl);
        datasourceConfig.setDrCls(drCls);
        datasourceConfig.setUser(user);
        datasourceConfig.setPwd(pwd);
        String fileType = PropertiesUtil.getString(AppConstant.CFG_DIC_FILETYPE).toUpperCase();
        String version = PropertiesUtil.getString(AppConstant.CFG_DIC_VERSION);
        String discription = PropertiesUtil
                .getString(AppConstant.CFG_DIC_DISCRIPTION);
        String name = PropertiesUtil.getString(AppConstant.CFG_DIC_NAME);
        DicFileConfig dicFileConfig = new DicFileConfig();
        dicFileConfig.setFileType(fileType);
        dicFileConfig.setVersion(version);
        dicFileConfig.setDiscription(discription);
        dicFileConfig.setName(name);
        tool.documentGeneration(datasourceConfig, dicFileConfig);
    }

    /**
     * 文档生成
     */
    void documentGeneration(DatasourceConfig dataSourceConfig,
            DicFileConfig dicFileConfig) {
        String fileOutputDir = System.getProperty("user.dir") + File.separator
                + AppConstant.DOC_DIR;

        // 数据源
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(dataSourceConfig.getDrCls());
        hikariConfig.setJdbcUrl(dataSourceConfig.getDbUrl());
        hikariConfig.setUsername(dataSourceConfig.getUser());
        hikariConfig.setPassword(dataSourceConfig.getPwd());
        // 设置可以获取tables remarks信息
        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaximumPoolSize(5);
        DataSource dataSource = new HikariDataSource(hikariConfig);
        // 生成配置
        EngineConfig engineConfig = EngineConfig.builder()
                // 生成文件路径
                .fileOutputDir(fileOutputDir)
                // 打开目录
                .openOutputDir(true)
                // 文件类型
                .fileType(getFileType(dicFileConfig.getFileType()))
                // 生成模板实现
                .produceType(EngineTemplateType.freemarker)
                // 自定义文件名称
                .fileName(dicFileConfig.getName()).build();

        // 忽略表
        ArrayList<String> ignoreTableName = new ArrayList<>();
        ignoreTableName.add("test_user");
        ignoreTableName.add("test_group");
        // 忽略表前缀
        ArrayList<String> ignorePrefix = new ArrayList<>();
        ignorePrefix.add("test_");
        // 忽略表后缀
        ArrayList<String> ignoreSuffix = new ArrayList<>();
        ignoreSuffix.add("_test");
        ProcessConfig processConfig = ProcessConfig.builder()
                // 指定生成逻辑、当存在指定表、指定表前缀、指定表后缀时，将生成指定表，其余表不生成、并跳过忽略表配置
                // 根据名称指定表生成
                .designatedTableName(new ArrayList<>())
                // 根据表前缀生成
                .designatedTablePrefix(new ArrayList<>())
                // 根据表后缀生成
                .designatedTableSuffix(new ArrayList<>())
                // 忽略表名
                .ignoreTableName(ignoreTableName)
                // 忽略表前缀
                .ignoreTablePrefix(ignorePrefix)
                // 忽略表后缀
                .ignoreTableSuffix(ignoreSuffix).build();
        // 配置
        Configuration config = Configuration.builder()
                // 版本
                .version(dicFileConfig.getVersion())
                // 描述
                .description(dicFileConfig.getDiscription())
                // 数据源
                .dataSource(dataSource)
                // 生成配置
                .engineConfig(engineConfig)
                // 生成配置
                .produceConfig(processConfig).build();
        // 执行生成
        if(AppConstant.FILE_TYPE_EXCEL.equalsIgnoreCase(dicFileConfig.getFileType())) {
            new ExcelFileExecute(config).execute();
        } else {
            new DocumentationExecute(config).execute();
        }
      
    }

    private EngineFileType getFileType(String fileType) {
        EngineFileType type;
        switch (fileType) {
        case AppConstant.FILE_TYPE_HTML:
            type = EngineFileType.HTML;
            break;
        case AppConstant.FILE_TYPE_MD:
            type = EngineFileType.MD;
            break;
        case AppConstant.FILE_TYPE_WORD:
            type = EngineFileType.WORD;
            break;
        default:
            type = EngineFileType.HTML;
            break;
        }
        return type;
    }
}
