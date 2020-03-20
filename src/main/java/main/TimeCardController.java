package main;
 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
	String postIndexForm(Model model) {		

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Principalからログインユーザの情報を取得
        String userId = auth.getName();
        
        try {
        	workingTimeList = timeCardRepo.findWorkingTime(userId);
        }catch(Exception e) {
        	e.printStackTrace();
        }
			
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
		
        try {
        	workingTimeList = timeCardRepo.findWorkingTime(workingTimeList.get(0).getUserId());
        }catch(Exception e) {
        	e.printStackTrace();
        }
        		
		
		// 本日かどうか判定
		if(workingTimeList.get(0).getDate().equals(sdf.format(cl.getTime()))) {

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
			
			// 日付と時間に分割
			String[] inTimeArray = inTime.split(",", 0);		

			// 年、月、日に分割
			String str = inTimeArray[0].replace("-", ",");	
			String[] dateArray = str.split(",", 0);
			
//			int intWeekDay;
			String strWeekDay = "";
			
			// 曜日を取得
//			intWeekDay = cl.get(Calendar.DAY_OF_WEEK);
			switch (cl.get(Calendar.DAY_OF_WEEK)) {
		        case Calendar.SUNDAY: strWeekDay = "日"; break;
		        case Calendar.MONDAY: strWeekDay = "月"; break;
		        case Calendar.TUESDAY: strWeekDay = "火"; break;
		        case Calendar.WEDNESDAY: strWeekDay = "水"; break;
		        case Calendar.THURSDAY: strWeekDay = "木" ; break;
		        case Calendar.FRIDAY: strWeekDay = "金"; break;
		        case Calendar.SATURDAY: strWeekDay = "土"; break;
		    }
			
			// 時刻を15分区切りに変換
			String[] timeArray = new String[3];
			timeArray = inTimeArray[1].split(":",0);
			int min = Integer.parseInt(timeArray[1]);
			int pause = 15;
			int workTime = (min / pause) * pause;
			
			if(inTimeArray[1].compareTo( "9:00:00") != 1) {
				timeArray[0] = "9";
				timeArray[1] = "00";
				timeArray[2] = "00";
			}else {
				timeArray[1] = String.valueOf(workTime);
				timeArray[2] = "00";
			}
			
			// 分割していた時刻を基の形に戻す
			String time = timeArray[0] + ":" + timeArray[1] + ":" + timeArray[2];
			
			if(!(inTime == "")) {
				try {
					timeCardRepo.insertWorkingTime(workingTimeList.get(0).getUserId(),
							inTimeArray[0],
							dateArray[0],
							dateArray[1],
							dateArray[2],
							strWeekDay,
							"",
							"",
							inTimeArray[1],
							"",
							time,
							"");
				}catch(Exception e) {
					e.printStackTrace();
				}
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
        
        try {
        	workingTimeList = timeCardRepo.findWorkingTime(workingTimeList.get(0).getUserId());
        }catch(Exception e) {
        	e.printStackTrace();
        }
		
		// 本日かどうか判定
		if(workingTimeList.get(0).getDate().toString().contentEquals(sdf.format(cl.getTime()).toString())) {

			// 出社打刻がされている場合は退勤打刻を行う
			if(!(workingTimeList.get(0).getInTime().isEmpty()) && workingTimeList.get(0).getOutTime().isEmpty()) {
				
				// 日付と時間に分割
				String[] outTimeArray = outTime.split(",", 0);
				
				// タイムカードNoを文字列に変換
				String timeCardNo = String.valueOf((workingTimeList.get(0).getTimeCardNo()));
				
				// 年、月、日に分割
				String str = outTimeArray[0].replace("-", ",");	
				String[] dateArray = str.split(",", 0);
								
				// 時刻を15分区切りに変換
				String[] timeArray = new String[3];
				timeArray = outTimeArray[1].split(":",0);
				int min = Integer.parseInt(timeArray[1]);
				int pause = 15;
				int workTime = (min / pause) * pause;
			
				timeArray[1] = String.valueOf(workTime);
				timeArray[2] = "00";
				
				// 分の単位を合わせる
				if(timeArray[1].equals("0")) {
					timeArray[1] = "00";
				}
				
				// 休憩時間
				String breakTime = "";
				// 残業時間
				String overTime = "";
				
				
				// 退勤時間(15分区切り)
				String strEndTime = timeArray[0] + ":" + timeArray[1] + ":" + timeArray[2];		

//				strEndTime = outTimeArray[0] + " " + strEndTime;
				
				// 出勤時間(15分区切り)
				String strInTime = workingTimeList.get(0).getWorkStartTime();

				String workStartTimeArray[] = strInTime.split(":", 0);
				String workEndTimeArray[] = strEndTime.split(":", 0);

				int startTime = Integer.parseInt(workStartTimeArray[0]) * 60 + Integer.parseInt(workStartTimeArray[1]) ;
				int endTime = Integer.parseInt(workEndTimeArray[0]) * 60 + Integer.parseInt(workEndTimeArray[1]);
				int diffTime = endTime - startTime;
				int overHour = diffTime / 60;
				int overMin = diffTime % 60;
						
				if(overHour >= 8) {
					breakTime = "1:00";

					switch (overMin) {
					case 0: overTime = String.valueOf(overHour - 9) + ":00"; break;
					case 15: overTime = String.valueOf(overHour - 9) + ":15"; break;
					case 30: overTime = String.valueOf(overHour - 9) + ":30"; break;
					case 45: overTime = String.valueOf(overHour - 9) + ":45"; break;
					}

				}else {
					breakTime = "0:00";
					overTime = "0:00";
				}
				// 残業時間を
//				SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
//				Date startDateTime = new Date();
//				Date endDateTime = new Date();
//				
//				
//				try {
//					startDateTime = formatter.parse(strInTime);
//					endDateTime = formatter.parse(workEndTime);
//				} catch (ParseException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				long diffTime = endDateTime.getTime() - startDateTime.getTime();
//				SimpleDateFormat timeFormatter = new SimpleDateFormat ("HH:mm:ss Z");
//				timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
//				
//				// 8時間以上の勤務時間
//				String strDiffTime = timeFormatter.format(new Date(diffTime - 32400000));
				
				
//				if(diffTime >= 32400000) {
//					breakTime = "1:00";
//					overTime = strDiffTime;
//				}else {
//					breakTime = "0:00";
//					overTime = "0:00";
//				}
//				// 休憩時間を算出
//				// 時間のみ取り出して数値に変換
//				int intWorkStartTime = Integer.parseInt((workingTimeList.get(0).getWorkStartTime()).substring(0, 1));
//				int intOutTime = Integer.parseInt((strOutTime));
//				int resultTime = intOutTime - intWorkStartTime;
//				


				try {
					timeCardRepo.updateOutTime(timeCardNo, breakTime, overTime, outTimeArray[1], strEndTime);
				}catch(Exception e) {
					e.printStackTrace();
				}
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
        
        try {
        // ユーザーIDと取得年月から対象データを取得
        	workingTimeList = timeCardRepo.findWorkingTime(userId, date);
        }catch(Exception e) {
        	e.printStackTrace();
        }

		model.addAttribute("userId", userId);
		model.addAttribute("year", workingTimeList.get(0).getYear() + "年");
		model.addAttribute("month", workingTimeList.get(0).getMonth() + "月");
        model.addAttribute("workingTimeList", workingTimeList);
		return "workingTimeTable";
	}

}