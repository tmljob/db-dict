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
     * ????????????
     */
    void documentGeneration(DatasourceConfig dataSourceConfig,
            DicFileConfig dicFileConfig) {
        String fileOutputDir = System.getProperty("user.dir") + File.separator
                + AppConstant.DOC_DIR;

        // ?????????
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(dataSourceConfig.getDrCls());
        hikariConfig.setJdbcUrl(dataSourceConfig.getDbUrl());
        hikariConfig.setUsername(dataSourceConfig.getUser());
        hikariConfig.setPassword(dataSourceConfig.getPwd());
        // ??????????????????tables remarks??????
        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaximumPoolSize(5);
        DataSource dataSource = new HikariDataSource(hikariConfig);
        // ????????????
        EngineConfig engineConfig = EngineConfig.builder()
                // ??????????????????
                .fileOutputDir(fileOutputDir)
                // ????????????
                .openOutputDir(true)
                // ????????????
                .fileType(getFileType(dicFileConfig.getFileType()))
                // ??????????????????
                .produceType(EngineTemplateType.freemarker)
                // ?????????????????????
                .fileName(dicFileConfig.getName()).build();

        // ?????????
        ArrayList<String> ignoreTableName = new ArrayList<>();
        ignoreTableName.add("test_user");
        ignoreTableName.add("test_group");
        // ???????????????
        ArrayList<String> ignorePrefix = new ArrayList<>();
        ignorePrefix.add("test_");
        // ???????????????
        ArrayList<String> ignoreSuffix = new ArrayList<>();
        ignoreSuffix.add("_test");
        ProcessConfig processConfig = ProcessConfig.builder()
                // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                // ???????????????????????????
                .designatedTableName(new ArrayList<>())
                // ?????????????????????
                .designatedTablePrefix(new ArrayList<>())
                // ?????????????????????
                .designatedTableSuffix(new ArrayList<>())
                // ????????????
                .ignoreTableName(ignoreTableName)
                // ???????????????
                .ignoreTablePrefix(ignorePrefix)
                // ???????????????
                .ignoreTableSuffix(ignoreSuffix).build();
        // ??????
        Configuration config = Configuration.builder()
                // ??????
                .version(dicFileConfig.getVersion())
                // ??????
                .description(dicFileConfig.getDiscription())
                // ?????????
                .dataSource(dataSource)
                // ????????????
                .engineConfig(engineConfig)
                // ????????????
                .produceConfig(processConfig).build();
        // ????????????
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
