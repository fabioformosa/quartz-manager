package it.fabioformosa.quartzmanager.services;

import it.fabioformosa.quartzmanager.jobs.AbstractQuartzManagerJob;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobService {

  @Getter
  private List<Class<? extends AbstractQuartzManagerJob>> jobClasses = new ArrayList<>();

  private List<String> jobClassPackages = new ArrayList<>();

  public JobService(@Value("${quartz-manager.jobClassPackages}") String jobClassPackages) {
    List<String> splitPackages = Arrays.stream(Optional.of(jobClassPackages).map(str -> str.split(",")).get())
      .map(String::trim)
      .filter(StringUtils::isNotBlank)
      .collect(Collectors.toList());
    if (splitPackages.size() > 0)
      this.jobClassPackages.addAll(splitPackages);
  }

  @PostConstruct
  public void initJobClassList() {
    List<Class<? extends AbstractQuartzManagerJob>> foundJobClasses = jobClassPackages.stream().flatMap(jobClassPackage -> findJobClassesInPackage(jobClassPackage).stream()).collect(Collectors.toList());
    if (foundJobClasses.size() > 0)
      this.jobClasses.addAll(foundJobClasses);
  }

  private static Set<Class<? extends AbstractQuartzManagerJob>> findJobClassesInPackage(String packageStr) {
    Reflections reflections = new Reflections(packageStr);
    return reflections.getSubTypesOf(AbstractQuartzManagerJob.class);
  }

}
