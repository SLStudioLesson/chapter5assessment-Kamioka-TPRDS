package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.taskapp.model.Log;

public class LogDataAccess {
    private final String filePath;


    public LogDataAccess() {
        filePath = "app/src/main/resources/logs.csv";
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     */
    public LogDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * ログをCSVファイルに保存します。
     *
     * @param log 保存するログ
     */
    public void save(Log log) {
        //logs.csvに受け取ったLogを1行で書き込む
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.newLine();
            writer.write(log.getTaskCode() + "," + log.getChangeUserCode() + "," +
                    log.getStatus() + "," + log.getChangeDate());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * すべてのログを取得します。
     *
     * @return すべてのログのリスト
     */
    public List<Log> findAll() {
        //logs.csvから全ての行を読み込みList<Log>として返します
        List<Log> logList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineData = line.split(",");
                logList.add(new Log(Integer.parseInt(lineData[0]), Integer.parseInt(lineData[1]),
                        Integer.parseInt(lineData[2]),LocalDate.parse(lineData[3])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logList;
    }

    /**
     * 指定したタスクコードに該当するログを削除します。
     *
     * @see #findAll()
     * @param taskCode 削除するログのタスクコード
     */
    public void deleteByTaskCode(int taskCode) {
        //csvファイルを読み込み、codeとコードが同じタスクは書き込まない
        //それ以外は読み込んだTaskを書き込む
        List<Log> logList = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Task_Code,Change_User_Code,Status,Change_Date");
            for (Log log : logList) {
                if (!(taskCode == log.getTaskCode())) {
                    writer.newLine();
                    writer.write(log.getTaskCode() + "," + log.getChangeUserCode() + "," +
                            log.getStatus() + "," + log.getChangeDate());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ログをCSVファイルに書き込むためのフォーマットを作成します。
     *
     * @param log フォーマットを作成するログ
     * @return CSVファイルに書き込むためのフォーマット
     */
    // private String createLine(Log log) {
    // }

}