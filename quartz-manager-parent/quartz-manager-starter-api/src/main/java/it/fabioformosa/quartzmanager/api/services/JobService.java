package it.fabioformosa.quartzmanager.api.services;

import it.fabioformosa.quartzmanager.api.jobs.AbstractQuartzManagerJob;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobService {

  @Getter
  private List<Class<? extends AbstractQuartzManagerJob>> jobClasses = new ArrayList<>();

  private List<String> jobClassPackages = new ArrayList<>();

  public JobService(@Value("${quartz-manager.jobClassPackages}") String jobClassPackages) {
    List<String> splitPackages = Arrays.stream(Optional.of(jobClassPackages).map(str -> str.split(","))
        .orElseThrow(() -> new RuntimeException("The prop quartz-manager.jobClassPackages  cannot be blank!")))
      .map(String::trim)
      .filter(StringUtils::isNotBlank)
      .collect(Collectors.toList());
    if (!splitPackages.isEmpty())
      this.jobClassPackages.addAll(splitPackages);
  }

  @PostConstruct
  public void initJobClassList() {
    List<Class<? extends AbstractQuartzManagerJob>> foundJobClasses = jobClassPackages.stream().flatMap(jobClassPackage -> findJobClassesInPackage(jobClassPackage).stream()).collect(Collectors.toList());
    if (!foundJobClasses.isEmpty()) {
      log.info("Found the following eligible job classes: {}", foundJobClasses);
      this.jobClasses.addAll(foundJobClasses);
    }
    else
      log.warn("Not found any eligible job classes!");
  }

  private static Set<Class<? extends AbstractQuartzManagerJob>> findJobClassesInPackage(String packageStr) {
    Reflections reflections = new Reflections(packageStr);
    return reflections.getSubTypesOf(AbstractQuartzManagerJob.class);
  }

}
