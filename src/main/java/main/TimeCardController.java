package main;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class TimeCardController {
	

//	TimeCardService service;
	@Autowired
	TimeCardRepository timeCardRepo;
	@Autowired
	UserInfoRepository userInfoRepo;
	
	List<UserInfoEntity> userData;
	List<WorkingTimeEntity> workingTimeList;
	
//	@RequestMapping(value = "/", method = RequestMethod.GET)
//	String index(Model model) {
//		List<WorkingTimeEntity> workingTimeList = timeCardRepo.findAll();
//		model.addAttribute("workingTimeList", workingTimeList);
//		model.addAttribute("inTimeText", "IN");
//		return "index";
//	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	String getLogin() {
		return "login";
	}
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	String getIndex() {
		return "index";
	}

//	@RequestMapping(value = "loginForm", method = RequestMethod.GET)
//	String getLoginForm(Model model) {
//		return "login";
//	}

	@RequestMapping(value = "/index", method = RequestMethod.POST)
	String postLoginForm(Model model,@RequestParam("id") String id, @RequestParam("pass") String pass) {		

		userData = userInfoRepo.findUserInfo(id, pass);		

		if (userData.isEmpty()) {
			model.addAttribute("userDataInfo", "ユーザIDもしくはパスワードが正しくありません。");
			return "login";
		} else {
			workingTimeList = timeCardRepo.findWorkingTime(id);
			
			if (!(workingTimeList.get(0).getOutTime().isEmpty())) {
				model.addAttribute("inTimeText", "IN");	
				model.addAttribute("outTimeText", "OUT");
				return "index";
			}
			
			if (workingTimeList.get(0).getInTime().isEmpty()) {
				model.addAttribute("inTimeText", "IN");	
			}else {
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());				
			}			
			if(workingTimeList.get(0).getOutTime().isEmpty()) {
				model.addAttribute("outTimeText", "OUT");
			}else {
				model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
			}
			model.addAttribute("userDataInfo", userData.get(0).getName());
			model.addAttribute("workingTimeList", workingTimeList);
			return "index";
		}
	}

	@RequestMapping(value = "/inTimeForm", method = RequestMethod.GET)
	public String getInTimeForm(
		@RequestParam(value="inTime", required = false)
		Model model) {
//		model.addAttribute("inTimeText", "IN");
		return "index";
	}

	@RequestMapping(value = "/inTimeForm", method = RequestMethod.POST)
	public String inTimeInsert( Model model, @RequestParam("inTime") String inTime) {
		
		workingTimeList = timeCardRepo.findWorkingTime(workingTimeList.get(0).getUserId());
		
		String[] inTimeArray = inTime.split(",", 0);
		
		if(workingTimeList.get(0).getInTime().isEmpty() && workingTimeList.get(0).getOutTime().isEmpty()) {
			timeCardRepo.insertWorkingTime(workingTimeList.get(0).getUserId(),
															inTimeArray[0],
															inTimeArray[1],
															" ");
			model.addAttribute("inTimeText", inTimeArray[1]);
			model.addAttribute("outTimeText", "OUT");
			return "index";

		}else if(!(workingTimeList.get(0).getInTime().isEmpty())) {
			model.addAttribute("inTimeText", inTimeArray[1]);
			model.addAttribute("outTimeText", "OUT");
			return "index";

		}else {
			model.addAttribute("inTimeText", "IN");
			model.addAttribute("outTimeText", "OUT");
			return "index";
		}
	}

//	@RequestMapping(value = "/inTimeForm", method = RequestMethod.POST)
//	public String inTimeInsert(
//		@RequestParam(value="inTime", required = false) String inTimeStr,
//		Model model) {
//		if (inTimeStr=="IN") {
//			
//		}
//		else {
//			model.addAttribute("inTimeText", inTimeStr);
//		}
//		return "index";
//	}
	
//	@RequestMapping(value = "/", method = RequestMethod.POST)
//	public String form(@RequestParam(name = "find", required = false) String inTime, Model model) {
//		List<WorkingTimeEntity> workingTimeList = repository.findByinTime(inTime);
//		model.addAttribute("value", inTime);
//		model.addAttribute("workingTimeList", workingTimeList);
//		return "index";
//	}
/*
	List<WorkingTimeEntity> getWorkingTimeData() {
		return service.findAll();
		}
*/
/*
	@RequestMapping("/")
    public String test (Model model) {
        Iterable<WorkingTimeEntity> list = repository.findAll();
        model.addAttribute("datas",list);
    return "helo";
*/
/*   
    @RequestMapping(value="/", method=RequestMethod.GET)
    public ModelAndView index(ModelAndView mav) {
        mav.setViewName("index");
        mav.addObject("msg", "input your name :");    // 表示メッセージ
        return mav;
    }

    @RequestMapping(value="/", method=RequestMethod.POST)
    public ModelAndView send(@RequestParam("name")String name, 
            ModelAndView mav) {
        mav.setViewName("index");
        mav.addObject("msg", "Hello " + name + " !");    // 表示メッセージ
        mav.addObject("value", name);                    // 入力テキストに入力値を表示
        return mav;
    }
 */
}