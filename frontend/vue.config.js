const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  outputDir: '../src/main/resources/static',
  indexPath: "../static/index.html",
  devServer: {
    port: '5173',
    proxy: {
      "/api" : {
        target : "http://192.168.0.122:8081",
        changeOrigin: true
      }
    }
  },
  chainWebpack:config => {
    const svgRule = config.module.rule("svg");
    svgRule.uses.clear();
    svgRule.use("vue-svg-loader").loader("vue-svg-loader");
  }
})
