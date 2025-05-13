package com.xunqi.gulimall;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @Description: MybatisPlus代码生成
 * @Author: cishuzheng
 * @Date: 2022/03/20 13:20
 **/
public class DaoCodeGenerator {



    public static void main(String[] args) {
        // 待生成表名
        String[] tables = new String[]{"sys_refund_record"};
        // 取消的前缀，用于Entity
        String[] tablePrefix = new String[]{"sys_"};
        String funcName = "refundrecord";// 功能分组名
        String author = "";// 作者名

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://127.0.0.1:3306/gulimall?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai");
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("root");

        DaoGeneBo daoGeneBo = new DaoGeneBo();
        daoGeneBo.setTables(tables);
        daoGeneBo.setTablePrefix(tablePrefix);
        daoGeneBo.setFuncName(funcName);
        daoGeneBo.setAuthor(author);
        invokeGeneratorMethod(daoGeneBo, dsc);
    }

    private static void invokeGeneratorMethod(DaoGeneBo daoGeneBo, DataSourceConfig dsc) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor(daoGeneBo.getAuthor());
        gc.setOpen(false);
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        gc.setServiceName("%sService");
        // 设置日期类型
        gc.setDateType(DateType.ONLY_DATE);
        // 设置文件强制覆盖
//        gc.setFileOverride(true);

        mpg.setGlobalConfig(gc);

        // 数据源配置
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
//        pc.setModuleName(scanner("模块名"));
//        pc.setParent("com.idc.ticket_auto");
        // 设置各个Controller、Service、实体类包路径
        String apFuncName = StringUtils.isNotBlank(daoGeneBo.getFuncName()) ? ("." + daoGeneBo.getFuncName()) : "";
        pc.setController("controller.fa" + apFuncName);
        pc.setService("service.fa" + apFuncName);
        pc.setServiceImpl("service.fa" + apFuncName + ".impl");
        pc.setEntity("entity.fa" + apFuncName);
        pc.setMapper("dao.fa" + apFuncName);

        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
//         String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/fa/" + daoGeneBo.getFuncName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileCreate((configBuilder, fileType, filePath) -> {
            // 如果是Entity则直接返回true表示写文件
            if (fileType == FileType.ENTITY) {
                return true;
            }
            // 否则先判断文件是否存在
            File file = new File(filePath);
            boolean exist = file.exists();
            if (!exist) {
                file.getParentFile().mkdirs();
            }
            // 文件不存在或者全局配置的 fileOverride 为 true 才写文件
            return !exist || configBuilder.getGlobalConfig().isFileOverride();
        });
        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录，自定义目录用");
                if (fileType == FileType.MAPPER) {
                    // 已经生成 mapper 文件判断存在，不想重新生成返回 false
                    return !new File(filePath).exists();
                }
                // 允许生成模板文件
                return true;
            }
        });
        */
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig stConfig = new StrategyConfig();
        stConfig.setCapitalMode(true) //全局大写命名
                .setNaming(NamingStrategy.underline_to_camel) // 数据库表映射到实体的命名策略
                .setTablePrefix(daoGeneBo.getTablePrefix()) //表前缀
                .setInclude(daoGeneBo.getTables())  // 生成的表
                .setEntityLombokModel(true);//支持Lombok

        mpg.setStrategy(stConfig);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

    /**
     * 读取控制台内容
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    @Data
    static class DaoGeneBo{
        private String[] tables;
        private String[] tablePrefix;
        private String funcName;
        private String author = "";
    }
}
