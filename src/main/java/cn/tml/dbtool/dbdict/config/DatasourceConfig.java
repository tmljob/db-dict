package cn.tml.dbtool.dbdict.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatasourceConfig {
    
    private String drCls;
    private String dbUrl;
    private String user;
    private String pwd;

}
