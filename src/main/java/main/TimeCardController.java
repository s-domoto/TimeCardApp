package main;
 
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
		Collections.reverse(workingTimeList);
		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Principalからログインユーザの情報を取得
        String userId = auth.getName();
				
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
        model.addAttribute("userId", userId);
		model.addAttribute("workingTimeTable", "勤務表");
		return "main";
	}

	@RequestMapping(value = "/workingTimeTable", method = RequestMethod.GET)
	String getWorkingTimeTable() {
		
		return "workingTimeTable";
	}

	@RequestMapping(value = "/main", method = RequestMethod.POST)
	String postIndexForm(Model model/*,@RequestParam("id") String id*/) {		
/*
		userData = userInfoRepo.findUserInfo(id, pass);		

		if (userData.isEmpty()) {
			model.addAttribute("userDataInfo", "ユーザIDもしくはパスワードが正しくありません。");
			return "index";
		} else {
*/
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Principalからログインユーザの情報を取得
        String userId = auth.getName();
        
        workingTimeList = timeCardRepo.findWorkingTime(userId);
			
        // 出社打刻・退社打刻ともにされていない場合は「IN」、「OUT」を表示
        if (!(workingTimeList.get(0).getInTime().isEmpty()) && !(workingTimeList.get(0).getOutTime().isEmpty())) {
        	model.addAttribute("userId", userId);
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
        model.addAttribute("userId", userId);
        model.addAttribute("workingTimeTable", "勤務表");
        return "main";
	}
//	}

	@RequestMapping(value = "/inTimeForm", method = RequestMethod.GET)
	public String getInTimeForm(
		@RequestParam(value="inTime", required = false)
		Model model) {
//		model.addAttribute("inTimeText", "IN");
		return "main";
	}

	@RequestMapping(value = "/inTimeForm", method = RequestMethod.POST)
	public String inTimeInsert( Model model, @RequestParam("inTime") String inTime) {
		
        Calendar cl = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Principalからログインユーザの情報を取得
        String userId = auth.getName();
		
		workingTimeList = timeCardRepo.findWorkingTime(workingTimeList.get(0).getUserId());
		
		
		// 日付と時間に分割
		String[] inTimeArray = inTime.split(",", 0);
		
		// 本日かどうか判定
		if(workingTimeList.get(0).getDate() == sdf.format(cl.getTime())) {

			// 出社時間が空でない、かつ退社時間が空の場合
			if(!(workingTimeList.get(0).getInTime().isEmpty()) && workingTimeList.get(0).getOutTime().isEmpty()) {
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
				model.addAttribute("outTimeText", "OUT");
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";

			// 出社時間が空でない、かつ退社時間が空でない
			}else if(!(workingTimeList.get(0).getInTime().isEmpty()) && !(workingTimeList.get(0).getOutTime().isEmpty())) {
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
				model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
				
			}else {
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", "IN");
				model.addAttribute("outTimeText", "OUT");
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
			}
			
		}else {
			// 日付が変わっていたら打刻する
			// 出社打刻・退社打刻ともにされていない場合は出社打刻する
//			if(!(workingTimeList.get(0).getInTime().isEmpty()) && !(workingTimeList.get(0).getOutTime().isEmpty())) {
			if(!(inTime == "")) {
				timeCardRepo.insertWorkingTime(workingTimeList.get(0).getUserId(),
						inTimeArray[0],
						inTimeArray[1],
						"");
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", inTimeArray[1]);
				model.addAttribute("outTimeText", "OUT");
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
			}else if(workingTimeList.get(0).getOutTime().isEmpty()) {
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
				model.addAttribute("outTimeText", "OUT");
				model.addAttribute("workingTimeTable", "勤務表");			
				return "main";
			}else {
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
				model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
			}
//			}
			
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

        Calendar cl = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Principalからログインユーザの情報を取得
        String userId = auth.getName();
		
		workingTimeList = timeCardRepo.findWorkingTime(workingTimeList.get(0).getUserId());
		// 日付と時間に分割
		String[] outTimeArray = outTime.split(",", 0);
		
		// タイムカードNoを文字列に変換
		String timeCardNo = String.valueOf((workingTimeList.get(0).getTimeCardNo()));
		
		// 本日かどうか判定
		if(workingTimeList.get(0).getDate().toString().contentEquals(sdf.format(cl.getTime()).toString())) {

			// 出社打刻がされている場合は退勤打刻を行う
			if(!(workingTimeList.get(0).getInTime().isEmpty()) && workingTimeList.get(0).getOutTime().isEmpty()) {

				timeCardRepo.updateOutTime(timeCardNo, outTimeArray[1]);
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
				model.addAttribute("outTimeText", outTimeArray[1]);
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
			}else if(workingTimeList.get(0).getInTime().isEmpty() && workingTimeList.get(0).getOutTime().isEmpty()){
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
				model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
			}else {
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", "IN");
				model.addAttribute("outTimeText", "OUT");
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
			}
		}else {
			if(workingTimeList.get(0).getInTime().isEmpty() && workingTimeList.get(0).getOutTime().isEmpty()) {
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", "IN");
				model.addAttribute("outTimeText", "OUT");
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
			}else if(workingTimeList.get(0).getInTime().isEmpty() && !(workingTimeList.get(0).getOutTime().isEmpty())) {
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", "IN");
				model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
			}else if(!(workingTimeList.get(0).getInTime().isEmpty() && workingTimeList.get(0).getOutTime().isEmpty())) {
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
				model.addAttribute("outTimeText", "OUT");
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
			}else {
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
				model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
			}
		}
	}		

	@RequestMapping(value = "/workingTimeTable", method = RequestMethod.POST)
	public String dispWorkingTimeTable(Model model) {
		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Principalからログインユーザの情報を取得
        String userId = auth.getName();
		
        model.addAttribute("userId", userId);
		return "workingTimeTable";
	}
	
	@RequestMapping(value = "/dateForm", method = RequestMethod.GET)
	public String getDate(Model model, @RequestParam("year") String year, @RequestParam("month") String month) {

		// SQL検索用に文字列を調整
		String date = year + "-" + month + "%";
		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Principalからログインユーザの情報を取得
        String userId = auth.getName();
        
        // ユーザーIDと取得年月から対象データを取得
		workingTimeList = timeCardRepo.findWorkingTime(userId, date);

		model.addAttribute("userId", userId);
        model.addAttribute("workingTimeList", workingTimeList);
		return "workingTimeTable";
	}

}