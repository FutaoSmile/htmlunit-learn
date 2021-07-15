package com.futao.htmlunit.learn.section01;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author ft
 * @date 2021/7/14
 */
public class TableParser {
    public static void main(String[] args) {

        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            // 配置
            WebClientOptions options = webClient.getOptions();
            //忽略js报错
            webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
            // 忽略css报错
            webClient.setCssErrorHandler(new SilentCssErrorHandler());
            //ajax
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            //支持js
            options.setJavaScriptEnabled(true);
            // js错误抛出异常
            options.setThrowExceptionOnScriptError(false);
            //忽略css错误
            webClient.setCssErrorHandler(new SilentCssErrorHandler());
            //不执行CSS渲染
            options.setCssEnabled(false);
            //超时时间
            options.setTimeout(3000);
            //允许重定向
            options.setRedirectEnabled(true);
            //允许cookie
            webClient.getCookieManager().setCookiesEnabled(true);

            TimeUnit.SECONDS.sleep(3);

            HtmlPage page = webClient.<HtmlPage>getPage("https://www.cbirc.gov.cn/branch/sichuan/view/pages/common/ItemDetail.html?docId=995962&itemId=2032");
            HtmlTable table = page.<HtmlTable>getFirstByXPath("//div[@class='Section0']/table");
            for (HtmlTableRow row : table.getRows()) {
                for (HtmlTableCell cell : row.getCells()) {
                    System.out.println("cell数据:" + cell.asNormalizedText() + "\t");
                }
                System.out.println("换行\n");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
