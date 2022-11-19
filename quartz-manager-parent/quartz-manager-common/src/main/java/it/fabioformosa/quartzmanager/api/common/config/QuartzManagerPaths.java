package it.fabioformosa.quartzmanager.api.common.config;

public class QuartzManagerPaths {

  private QuartzManagerPaths(){
  }

  public static final String QUARTZ_MANAGER_BASE_CONTEXT_PATH = "/quartz-manager";
  public static final String WEBJAR_PATH = "/quartz-manager-ui";

  public static final String QUARTZ_MANAGER_AUTH_PATH = QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/auth";
  public static final String QUARTZ_MANAGER_LOGIN_PATH = QUARTZ_MANAGER_AUTH_PATH + "/login";
  public static final String QUARTZ_MANAGER_LOGOUT_PATH = QUARTZ_MANAGER_AUTH_PATH + "/logout";


}
