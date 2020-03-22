package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
	String getWorkingTimeTable(Model model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//Principalからログインユーザの情報を取得
		String userId = auth.getName();
		
		model.addAttribute("userId", userId);
		return "workingTimeTable";
	}

	@RequestMapping(value = "/main", method = RequestMethod.POST)
	String postIndexForm(Model model) {

		Calendar cl = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//Principalからログインユーザの情報を取得
		String userId = auth.getName();

		try {
			workingTimeList = timeCardRepo.findWorkingTime(userId);
		}catch(Exception e) {
			e.printStackTrace();
		}

		// 本日かどうか判定
		if(workingTimeList.get(0).getDate().equals(sdf.format(cl.getTime()))) {

			// 出社打刻・退社打刻ともにされていない場合は「IN」、「OUT」を表示
			if ((workingTimeList.get(0).getInTime().isEmpty()) && (workingTimeList.get(0).getOutTime().isEmpty())) {
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
		model.addAttribute("userId", userId);
		model.addAttribute("inTimeText", "IN");	
		model.addAttribute("outTimeText", "OUT");
		model.addAttribute("workingTimeTable", "勤務表");

		return "main";
	}
	//	}

	@RequestMapping(value = "/inTimeForm", method = RequestMethod.GET)
	public String getInTimeForm(
			@RequestParam(value="inTime", required = false)
			Model model) {
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

			// 出勤打刻が9時前かどうか判定、9時よりも前なら出社時間は9時とする
			int temp = Integer.parseInt(inTimeArray[1].substring(0, 2).replace(":", ""));

			if(temp < 9) {
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
		// 本日の場合は出社打刻の有無をチェック
		if(workingTimeList.get(0).getDate().toString().contentEquals(sdf.format(cl.getTime()).toString())) {

			// 本日で出社打刻がされている場合は退勤打刻を行う
			if(!(workingTimeList.get(0).getInTime().isEmpty()) && workingTimeList.get(0).getOutTime().isEmpty()) {

				// 日付と時間に分割
				String[] outTimeArray = outTime.split(",", 0);

				// タイムカードNoを文字列に変換
				String timeCardNo = String.valueOf((workingTimeList.get(0).getTimeCardNo()));

				// 年、月、日に分割
				//				String str = outTimeArray[0].replace("-", ",");	
				//				String[] dateArray = str.split(",", 0);

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
				// 本日で出社打刻と退社打刻の両方がされていない場合はIN、OUTを表示
			}else if(workingTimeList.get(0).getInTime().isEmpty() && workingTimeList.get(0).getOutTime().isEmpty()){
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", "IN");
				model.addAttribute("outTimeText", "OUT");
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
				// 本日で出社打刻と退社打刻の両方がされている場合はその時間を表示
			}else {
				model.addAttribute("userId", userId);
				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
				model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
				model.addAttribute("workingTimeTable", "勤務表");
				return "main";
			}
			// 本日ではない場合
		}else {
			model.addAttribute("userId", userId);
			model.addAttribute("inTimeText", "IN");
			model.addAttribute("outTimeText", "OUT");
			model.addAttribute("workingTimeTable", "勤務表");
			return "main";
			//			if(workingTimeList.get(0).getInTime().isEmpty() && workingTimeList.get(0).getOutTime().isEmpty()) {
			//				model.addAttribute("userId", userId);
			//				model.addAttribute("inTimeText", "IN");
			//				model.addAttribute("outTimeText", "OUT");
			//				model.addAttribute("workingTimeTable", "勤務表");
			//				return "main";
			//			}else if(workingTimeList.get(0).getInTime().isEmpty() && !(workingTimeList.get(0).getOutTime().isEmpty())) {
			//				model.addAttribute("userId", userId);
			//				model.addAttribute("inTimeText", "IN");
			//				model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
			//				model.addAttribute("workingTimeTable", "勤務表");
			//				return "main";
			//			}else if(!(workingTimeList.get(0).getInTime().isEmpty() && workingTimeList.get(0).getOutTime().isEmpty())) {
			//				model.addAttribute("userId", userId);
			//				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
			//				model.addAttribute("outTimeText", "OUT");
			//				model.addAttribute("workingTimeTable", "勤務表");
			//				return "main";
			//			}else {
			//				model.addAttribute("userId", userId);
			//				model.addAttribute("inTimeText", workingTimeList.get(0).getInTime());
			//				model.addAttribute("outTimeText", workingTimeList.get(0).getOutTime());
			//				model.addAttribute("workingTimeTable", "勤務表");
			//				return "main";
			//			}
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

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//Principalからログインユーザの情報を取得
		String userId = auth.getName();

		if(year.isEmpty() || month.isEmpty()) {
			model.addAttribute("userId", userId);
			return "workingTimeTable";
		}else {

			// SQL検索用に文字列を調整
			String date = year + "-" + month + "%";

			workingTimeList.get(0).setYear(year);
			workingTimeList.get(0).setMonth(month);

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

	@RequestMapping(value = "/csvOutForm", method = RequestMethod.POST)
	public String getOutCsv(Model model) {

		// SQL検索用に文字列を調整
		String date = workingTimeList.get(0).getYear() + "-" + workingTimeList.get(0).getMonth() + "%";

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//Principalからログインユーザの情報を取得
		String userId = auth.getName();

		try {
			// ユーザーIDと取得年月から対象データを取得
			workingTimeList = timeCardRepo.findWorkingTime(userId, date);

			try {

				// 出力ファイルの作成
				FileWriter f = new FileWriter("C:\\sample\\kintai.csv", false);
				PrintWriter p = new PrintWriter(new BufferedWriter(f));

				// 内容をセットする
				for(int i = 0; i < workingTimeList.size(); i++){
					p.print(workingTimeList.get(i).getUserId());
					p.print(",");
					p.print(workingTimeList.get(i).getDate());
					p.print(",");
					p.print(workingTimeList.get(i).getYear());
					p.print(",");
					p.print(workingTimeList.get(i).getMonth());
					p.print(",");
					p.print(workingTimeList.get(i).getDay());
					p.print(",");
					p.print(workingTimeList.get(i).getWeekDay());
					p.print(",");
					p.print(workingTimeList.get(i).getBreakTime());
					p.print(",");
					p.print(workingTimeList.get(i).getOverTime());
					p.print(",");
					p.print(workingTimeList.get(i).getInTime());
					p.print(",");
					p.print(workingTimeList.get(i).getOutTime());
					p.print(",");
					p.print(workingTimeList.get(i).getWorkStartTime());
					p.print(",");
					p.print(workingTimeList.get(i).getWorkEndTime());
					p.print(",");

					p.println();    // 改行
				}


				// ファイルに書き出し閉じる
				p.close();

				System.out.println("ファイル出力完了！");

			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}catch(Exception e) {
			e.printStackTrace();
		}

		return "workingTimeTable";
	}

	@RequestMapping(value = "/csvInForm", method = RequestMethod.POST)
	public String getInCsv(Model model) {

		try {
			File f = new File("C:\\sample\\kintai.csv");
			BufferedReader br = new BufferedReader(new FileReader(f));

			String line;

			// 1行ずつCSVファイルを読み込む
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",", 0); // 行をカンマ区切りで配列に変換

				// nullは空文字に変換
				for(int i = 0; i < data.length; i++) {
					if(data[i].equals("null")) {
						data[i] = data[i].replace("null", "");
					}
				}

				// CSVファイルのデータをインサート
				timeCardRepo.insertWorkingTime(
						data[0],
						data[1],
						data[2],
						data[3],
						data[4],
						data[5],
						data[6],
						data[7],
						data[8],
						data[9],
						data[10],
						data[11]);	
			}
			br.close();

		} catch (Exception e) {
			System.out.println(e);
		}

		return "workingTimeTable";
	}
	/*
	@RequestMapping(value = "/excelForm", method = RequestMethod.POST)
	public String getExcel(Model model) {

		WorkingTimeExcelView excelView = new WorkingTimeExcelView();
		try {

		} catch (Exception e) {
			System.out.println(e);
		}

		return "workingTimeTable";
	}
	 */
}