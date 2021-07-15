package com.futao.htmlunit.learn.section01;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 模拟登录
 *
 * @author ft
 * @date 2021/7/14
 */
public class ZhiHuHot {
    public static void main(String[] args) {
        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            // 配置
            WebClientOptions options = webClient.getOptions();
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

            // 抓取知乎热榜
            HtmlPage hotPage = webClient.<HtmlPage>getPage("https://www.zhihu.com/hot");
            // 网页文本（可通过这个方法来查看抓取的数据正确与否）
            String text = hotPage.asNormalizedText();
            // 网页源代码 html（可通过这个方法来查看抓取的数据正确与否）
            String htmlXml = hotPage.asXml();
            // 未登录状态下访问/hot页面会重定向到登录页面，所以当前htmlPage是登录页面
            TimeUnit.SECONDS.sleep(2);
            HtmlDivision htmlDivision = hotPage.<HtmlDivision>getFirstByXPath("//div[@class='SignFlow-tabs']/div[@class='SignFlow-tab']");
            HtmlPage pwdLoginPage = htmlDivision.click();
            HtmlForm loginForm = (HtmlForm) pwdLoginPage.getElementsByTagName("form").get(0);
            // 用户名输入框
            HtmlInput usernameInput = loginForm.getInputByName("username");
            // 输入用户名文本
            usernameInput.setValueAttribute("***");
            // 密码输入框
            HtmlInput passwordInput = loginForm.getInputByName("password");
            // 输入密码文本
            passwordInput.setValueAttribute("****");
            // 登录按钮
            HtmlButton loginBtn = loginForm.<HtmlButton>getFirstByXPath("//button[@type='submit']");
            // 点击登录按钮 - 返回登录之后的页面
            HtmlPage realHotPage = loginBtn.<HtmlPage>click();
            webClient.waitForBackgroundJavaScript(Duration.ofSeconds(3).getSeconds() * 1000);
            // 网页文本（可通过这个方法来查看抓取的数据正确与否）
            String realPageText = realHotPage.asNormalizedText();
            // 网页源代码 html（可通过这个方法来查看抓取的数据正确与否）
            String realPageHtmlXml = realHotPage.asXml();
            // 每一个section标签就对应一篇文章
            List<Object> sectionList = hotPage.getByXPath("//div[@class='HotList-list']//section");
            for (Object o : sectionList) {
                HtmlElement curArticle = (HtmlElement) o;
                // 获取序号
                String index = curArticle.<HtmlElement>getFirstByXPath("//dic[@class='HotItem-index']").asNormalizedText();
                // 获取标题
                String title = curArticle.<HtmlHeader>getFirstByXPath("//dic[@class='HotItem-content']/a/h2").asNormalizedText();
                // 链接
                String href = curArticle.<HtmlAnchor>getFirstByXPath("//dic[@class='HotItem-content']/a").getHrefAttribute();
                // 获取简介
                String description = curArticle.<HtmlParagraph>getFirstByXPath("//dic[@class='HotItem-content']/a/p").asNormalizedText();
                // 热度
                String hot = curArticle.<HtmlElement>getFirstByXPath("//dic[@class='HotItem-metrics HotItem-metrics--bottom']/svg").asNormalizedText();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
