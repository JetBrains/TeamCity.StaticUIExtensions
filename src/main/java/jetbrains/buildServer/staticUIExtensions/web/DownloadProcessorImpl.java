package jetbrains.buildServer.staticUIExtensions.web;

import jetbrains.buildServer.controllers.HttpDownloadProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class DownloadProcessorImpl implements DownloadProcessor {
  private final HttpDownloadProcessor myProcessor;

  public DownloadProcessorImpl(@NotNull final HttpDownloadProcessor processor) {
    myProcessor = processor;
  }

  @Nullable
  @Override
  public ModelAndView processFileDownload(@NotNull final File file,
                                          final boolean contentDisposition,
                                          @NotNull final HttpServletRequest request,
                                          @NotNull final HttpServletResponse response) throws IOException {
    return myProcessor.processFileDownload(file, true, request, response, null);
  }
}
