package jetbrains.buildServer.staticUIExtensions.web;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public interface DownloadProcessor {
  @Nullable
  ModelAndView processFileDownload(@NotNull final File file,
                                   boolean contentDisposition,
                                   @NotNull final HttpServletRequest request,
                                   @NotNull final HttpServletResponse response) throws Exception;
}
