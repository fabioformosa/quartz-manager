package it.fabioformosa.quartzmanager.controllers;

import javax.annotation.Resource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/manager")
public class ManagerController {

	public enum SchedulerStates {
		RUNNING, STOPPED, PAUSED
	}

	@Resource
	private Scheduler scheduler;

	@RequestMapping
	public ModelAndView getPanelView() throws SchedulerException {
		ModelAndView mav = new ModelAndView("panelView");

		String schedulerState;
		if (scheduler.isShutdown() || !scheduler.isStarted())
			schedulerState = SchedulerStates.STOPPED.toString();
		else if (scheduler.isStarted() && scheduler.isInStandbyMode())
			schedulerState = SchedulerStates.PAUSED.toString();
		else
			schedulerState = SchedulerStates.RUNNING.toString();

		mav.addObject("schedulerState", schedulerState.toLowerCase());

		return mav;
	}

	//	@MessageMapping("/updates")
	//	@SendTo("/topic/greetings")
	//	public String greeting(String message) throws Exception {
	//		Thread.sleep(3000); // simulated delay
	//		return "Hello";
	//	}

}
