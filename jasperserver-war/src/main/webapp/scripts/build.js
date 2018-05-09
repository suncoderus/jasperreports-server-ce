({
  "dir": "build/optimized/",
  "mainConfigFile": "require.config.js",
  "optimizeCss": "none",
  "optimize": "uglify2",
  "skipDirOptimize": false,
  "removeCombined": false,
  "preserveLicenseComments": false,
  "paths": {
    "common": "bower_components/js-sdk/src/common",
    "jquery": "empty:",
    "prototype": "empty:",
    "report.global": "empty:",
    "wcf.scroll": "empty:",
    "ReportRequireJsConfig": "empty:"
  },
  "modules": [
    {
      "name": "commons.main"
    },
    {
      "name": "addResource/dataType/addDataTypeMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/fileResource/addFileResourceMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/inputControls/addInputControlMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/inputControls/addInputControlQueryInformationMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/inputControls/dataTypeLocateMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/inputControls/listOfValuesLocateMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/jasperReport/addJasperReportMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/jasperReport/addJasperReportLocateControlMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/jasperReport/addJasperReportResourceNamingMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/jasperReport/addJasperReportResourcesAndControlsMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/listOfValues/addListOfValuesMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/mondrianXml/addMondrianXmlMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/analysisView/addOLAPViewMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/query/addQueryMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/query/addQueryWithResourceLocatorMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/analysisClientConnection/addAnalysisClientConnectionMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "administer/administerCustomAttributesMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "administer/administerExportMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "administer/administerImportMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "administer/administerLoggingMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "administer/administerAnalysisOptionsMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "administer/resetSettings/resetSettingsMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "manage/manageRolesMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "manage/manageUsersMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "dataSource/dataSourceMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/analysisClientConnection/locateDataSourceMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/analysisClientConnection/locateMondrianConnectionSourceMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/query/locateQueryMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "addResource/analysisClientConnection/locateXmlConnectionSourceMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "login/loginMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "olapView/olapViewMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "reportViewer/reportViewerMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "repository/repositoryMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "messages/details/messageDetailsMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "messages/list/messageListMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "scheduler/schedulerMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "encrypt/encryptMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "system/systemErrorMain",
      "exclude": [
        "commons.main"
      ]
    },
    {
      "name": "system/errorMain",
      "exclude": [
        "commons.main"
      ]
    }
  ],
  "fileExclusionRegExp": /(^\.|prototype.*patched\.js|Owasp\.CsrfGuard\.js)/,
  "shim": {
    "mustache": {
      "init": function () {
                    return Mustache;
                }
    }
  }
})