package main;
 
import java.util.Collection;
import java.util.Collections;
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
	String getIndex(Model model) {
		Collections.reverse(workingTimeList);;
		
		if(workingTimeList.get(0).getInTime().isEmpty()) {
			model.addAttribute("inTimeText", "IN");
		}else {
			model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
		}
		if(workingTimeList.get(0).getOutTime().isEmpty()) {
			model.addAttribute("outTimeText", "OUT");
		}else {
			model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
		}
		model.addAttribute("workingTimeTable", "勤務表");
		return "index";
	}

	@RequestMapping(value = "/workingTimeTable", method = RequestMethod.GET)
	String getWorkingTimeTable() {
		
		return "workingTimeTable";
	}

	@RequestMapping(value = "/index", method = RequestMethod.POST)
	String postLoginForm(Model model,@RequestParam("id") String id, @RequestParam("pass") String pass) {		

		userData = userInfoRepo.findUserInfo(id, pass);		

		if (userData.isEmpty()) {
			model.addAttribute("userDataInfo", "ユーザIDもしくはパスワードが正しくありません。");
			return "login";
		} else {
			workingTimeList = timeCardRepo.findWorkingTime(id);
			
			// 出社打刻・退社打刻ともにされていない場合は「IN」、「OUT」をj表示
			if (!(workingTimeList.get(0).getInTime().isEmpty()) && !(workingTimeList.get(0).getOutTime().isEmpty())) {
				model.addAttribute("inTimeText", "IN");	
				model.addAttribute("outTimeText", "OUT");
				model.addAttribute("workingTimeTable", "勤務表");
				return "index";
			}
			
			// 出社打刻がされていない場合は「IN」、されている場合は出社時間を表示
			if (workingTimeList.get(0).getInTime().isEmpty()) {
				model.addAttribute("inTimeText", "IN");	
			}else {
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());				
			}
			//　退社打刻がされていない場合は「OUT」、されている場合は退社時間を表示
			if(workingTimeList.get(0).getOutTime().isEmpty()) {
				model.addAttribute("outTimeText", "OUT");
			}else {
				model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
			}
			// ユーザー名を表示
			model.addAttribute("userDataInfo", userData.get(0).getName());
			// 確認要
//			model.addAttribute("workingTimeList", workingTimeList);
			model.addAttribute("workingTimeTable", "勤務表");
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
		
		// 日付と時間に分割
		String[] inTimeArray = inTime.split(",", 0);
		// 出社打刻・退社打刻ともにされていない場合は出社打刻する
		if(workingTimeList.get(0).getInTime().isEmpty() && workingTimeList.get(0).getOutTime().isEmpty()) {
			timeCardRepo.insertWorkingTime(workingTimeList.get(0).getUserId(),
															inTimeArray[0],
															inTimeArray[1],
															"");
			model.addAttribute("inTimeText", inTimeArray[1]);
			model.addAttribute("outTimeText", "OUT");
			return "index";

		}else if(!(workingTimeList.get(0).getInTime().isEmpty()) || workingTimeList.get(0).getOutTime().isEmpty()) {
			model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
			model.addAttribute("outTimeText", "OUT");
			return "index";

		}else if(!(workingTimeList.get(0).getInTime().isEmpty()) || !(workingTimeList.get(0).getOutTime().isEmpty())) {
			model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
			model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
			return "index";
			
		}else {
			model.addAttribute("inTimeText", "IN");
			model.addAttribute("outTimeText", "OUT");
			return "index";
		}
	}

	@RequestMapping(value = "/outTimeForm", method = RequestMethod.GET)
	public String getOutTimeForm(
		@RequestParam(value="outTime", required = false)
		Model model) {
		return "index";
	}

	@RequestMapping(value = "/outTimeForm", method = RequestMethod.POST)
	public String outTimeUpdate( Model model, @RequestParam("outTime") String outTime) {
		
		workingTimeList = timeCardRepo.findWorkingTime(workingTimeList.get(0).getUserId());
		String[] outTimeArray = outTime.split(",", 0);
		String timeCardNo = String.valueOf((workingTimeList.get(0).getTimeCardNo()));
		
		if(workingTimeList.get(0).getOutTime().isEmpty()) {
			if(workingTimeList.get(0).getDate() != outTimeArray[0]) {
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
				model.addAttribute("outTimeText", "OUT");
//				model.addAttribute("outTimeText", "日付が違うため退社打刻はできません。");
				return "index";
			}else {
			timeCardRepo.updateOutTime(timeCardNo,
										outTimeArray[1]);
			model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
			model.addAttribute("outTimeText", outTimeArray[1]);
			return "index";
			}
		}else {
			model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
			return "index";
		}
	}

	@RequestMapping(value = "/workingTimeTable", method = RequestMethod.POST)
	public String dispWorkingTimeTable(Model model) {
		workingTimeList = timeCardRepo.findAllWorkingTime(workingTimeList.get(0).getUserId());
		model.addAttribute("workingTimeList", workingTimeList);
		return "workingTimeTable";
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