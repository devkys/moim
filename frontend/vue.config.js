const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  outputDir: '../src/main/resources/static',
  indexPath: "index.html",
  devServer: {
    client: {
      overlay: false
    },
    port: '5173',
    proxy: {
      "/api" : {
        target : "http://localhost:8080",
        changeOrigin: true
      }
    }
  },
  // chainWebpack:config => {
  //   const svgRule = config.module.rule("svg");
  //   svgRule.uses.clear();
  //   svgRule.use("vue-svg-loader").loader("vue-svg-loader");
  // }
})
