package com.jetbrains.lang.dart.ide.runner.server;

import com.intellij.ide.browsers.BrowserFamily;
import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.browsers.WebBrowser;
import com.intellij.ide.browsers.WebBrowserManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.Computable;
import com.jetbrains.lang.dart.DartBundle;
import icons.DartIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public class OpenDartMemoryDashboardUrlAction extends DumbAwareAction {
  @Nullable private String myUrl;
  private final Computable<Boolean> myIsApplicable;

  /**
   * @param url <code>null</code> if URL is not known at the moment of the action instantiation; use {@link #setUrl(String)} afterwards
   */
  public OpenDartMemoryDashboardUrlAction(@Nullable final String url, @NotNull final Computable<Boolean> isApplicable) {
    super(DartBundle.message("open.memory.dashboard.action.text"),
          DartBundle.message("open.memory.dashboard.action.description"),
          DartIcons.Observatory_Memory);
    myUrl = url;
    myIsApplicable = isApplicable;
  }

  public void setUrl(@NotNull final String url) {
    myUrl = url;
  }

  @Override
  public void update(@NotNull final AnActionEvent e) {
    e.getPresentation().setEnabled(myUrl != null && myIsApplicable.compute());
  }

  @Override
  public void actionPerformed(@NotNull final AnActionEvent e) {
    if (myUrl != null) {
      String url = myUrl +  "#/memory-dashboard?editor=IntelliJ";
      final List<WebBrowser> chromeBrowsers = WebBrowserManager.getInstance().getBrowsers(
        browser -> browser.getFamily() == BrowserFamily.CHROME, false);
      try {
        Runtime.getRuntime().exec(chromeBrowsers.get(0).getPath() + " --new-window --window-position+0,0 --window-size=800,600 " + url.replace("\n",""));
      }
      catch (IOException e1) {
        BrowserLauncher.getInstance().browse(url, chromeBrowsers.isEmpty() ? null : chromeBrowsers.get(0));
      }
    }
  }
}
