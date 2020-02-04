package main;
 
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
	

	@Autowired
	TimeCardRepository timeCardRepo;
	@Autowired
	UserInfoRepository userInfoRepo;
	
	List<UserInfoEntity> userData;
	List<WorkingTimeEntity> workingTimeList;


	@RequestMapping(value = "/index", method = RequestMethod.GET)
	String getIndex() {
		return "index";
	}
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	String getMain(Model model) {
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
		return "main";
	}

	@RequestMapping(value = "/workingTimeTable", method = RequestMethod.GET)
	String getWorkingTimeTable() {
		
		return "workingTimeTable";
	}

	@RequestMapping(value = "/main", method = RequestMethod.POST)
	String postIndexForm(Model model,@RequestParam("id") String id, @RequestParam("pass") String pass) {		

		userData = userInfoRepo.findUserInfo(id, pass);		

		if (userData.isEmpty()) {
			model.addAttribute("userDataInfo", "ユーザIDもしくはパスワードが正しくありません。");
			return "index";
		} else {
			workingTimeList = timeCardRepo.findWorkingTime(id);
			
			// 出社打刻・退社打刻ともにされていない場合は「IN」、「OUT」をj表示
			if (!(workingTimeList.get(0).getInTime().isEmpty()) && !(workingTimeList.get(0).getOutTime().isEmpty())) {
				model.addAttribute("inTimeText", "IN");	
				model.addAttribute("outTimeText", "OUT");
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
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
			return "main";
		}
	}

	@RequestMapping(value = "/inTimeForm", method = RequestMethod.GET)
	public String getInTimeForm(
		@RequestParam(value="inTime", required = false)
		Model model) {
//		model.addAttribute("inTimeText", "IN");
		return "main";
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
			return "main";

		}else if(!(workingTimeList.get(0).getInTime().isEmpty()) || workingTimeList.get(0).getOutTime().isEmpty()) {
			model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
			model.addAttribute("outTimeText", "OUT");
			return "main";

		}else if(!(workingTimeList.get(0).getInTime().isEmpty()) || !(workingTimeList.get(0).getOutTime().isEmpty())) {
			model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
			model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
			return "main";
			
		}else {
			model.addAttribute("inTimeText", "IN");
			model.addAttribute("outTimeText", "OUT");
			return "main";
		}
	}

	@RequestMapping(value = "/outTimeForm", method = RequestMethod.GET)
	public String getOutTimeForm(
		@RequestParam(value="outTime", required = false)
		Model model) {
		return "main";
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
				return "main";
			}else {
			timeCardRepo.updateOutTime(timeCardNo,
										outTimeArray[1]);
			model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
			model.addAttribute("outTimeText", outTimeArray[1]);
			return "main";
			}
		}else {
			model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
			return "main";
		}
	}

	@RequestMapping(value = "/workingTimeTable", method = RequestMethod.POST)
	public String dispWorkingTimeTable(Model model) {
		workingTimeList = timeCardRepo.findAllWorkingTime(workingTimeList.get(0).getUserId());
		model.addAttribute("workingTimeList", workingTimeList);
		return "workingTimeTable";
	}

}