package cn.smallbun.screw.core.execute;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.exception.BuilderException;
import cn.smallbun.screw.core.metadata.model.DataModel;
import cn.smallbun.screw.core.process.DataModelProcess;
import cn.smallbun.screw.core.util.ExceptionUtils;
import cn.tml.dbtool.dbdict.engine.ExcelTemplateEngine;

public class ExcelFileExecute extends AbstractExecute {

    public ExcelFileExecute(Configuration config) {
        super(config);
    }

    @Override
    public void execute() throws BuilderException {
        try {
            long start = System.currentTimeMillis();
            // 处理数据
            DataModel dataModel = new DataModelProcess(config).process();
            // 产生文档
            ExcelTemplateEngine excelEngine = new ExcelTemplateEngine(
                    config.getEngineConfig());
            excelEngine.produce(dataModel, getDocName(dataModel.getDatabase()));
            logger.debug(
                    "database document generation complete time consuming:{}ms",
                    System.currentTimeMillis() - start);
        } catch (Exception e) {
            throw ExceptionUtils.mpe(e);
        }
    }

}
