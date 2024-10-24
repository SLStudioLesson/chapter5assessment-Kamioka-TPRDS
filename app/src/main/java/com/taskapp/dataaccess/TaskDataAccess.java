package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskDataAccess {

    private final String filePath;

    private final UserDataAccess userDataAccess;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     * @param userDataAccess
     */
    public TaskDataAccess(String filePath, UserDataAccess userDataAccess) {
        this.filePath = filePath;
        this.userDataAccess = userDataAccess;
    }

    /**
     * CSVから全てのタスクデータを取得します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @return タスクのリスト
     */
    public List<Task> findAll() {
        //tasks.csvから全ての行を読み込みList<Task>として返します
        //TaskクラスのUserクラスはfindByCodeメソッドを用いて探索します
        List<Task> taskList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineData = line.split(",");
                int userCode = Integer.parseInt(lineData[3]);
                User repUser = userDataAccess.findByCode(userCode);
                taskList.add(new Task(Integer.parseInt(lineData[0]), lineData[1], Integer.parseInt(lineData[2]), repUser));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return taskList;
    }

    /**
     * タスクをCSVに保存します。
     * @param task 保存するタスク
     */
    public void save(Task task) {
        //Taskクラスを受け取りtasks.csvに行を追加して1行で書き込む
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.newLine();
            writer.write(createLine(task));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを1件取得します。
     * @param code 取得するタスクのコード
     * @return 取得したタスク
     */
    public Task findByCode(int code) {
        //tasks.csvを1行ずつ読み込み、codeが合致した場合にTaskインスタンスを作成しreturnする
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] lineData = line.split(",");
                int taskCode = Integer.parseInt(lineData[0]);
                if (taskCode == code) {
                    Task task = new Task(taskCode, lineData[1], Integer.parseInt(lineData[2]),
                            userDataAccess.findByCode(Integer.parseInt(lineData[3])));
                    return task;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return null;
    }

    /**
     * タスクデータを更新します。
     * @param updateTask 更新するタスク
     */
    public void update(Task updateTask) {
        //csvファイルを読み込み、updateTaskとコードが同じタスクはupdateTaskを書き込む
        //それ以外は読み込んだTaskを書き込む
        List<Task> taskList = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Code,Name,Status,Rep_User_Code");
            for (Task task : taskList) {
                if (updateTask.getCode() == task.getCode()) {
                    writer.newLine();
                    writer.write(createLine(updateTask));
                } else {
                    writer.newLine();
                    writer.write(createLine(task));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを削除します。
     * @param code 削除するタスクのコード
     */
    public void delete(int code) {
        //csvファイルを読み込み、codeとコードが同じタスクは書き込まない
        //それ以外は読み込んだTaskを書き込む
        List<Task> taskList = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Code,Name,Status,Rep_User_Code");
            for (Task task : taskList) {
                if (!(code == task.getCode())) {
                    writer.newLine();
                    writer.write(createLine(task));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * タスクデータをCSVに書き込むためのフォーマットを作成します。
     * @param task フォーマットを作成するタスク
     * @return CSVに書き込むためのフォーマット文字列
     */
    private String createLine(Task task) {
        return String.join(",", Integer.toString(task.getCode()), task.getName(),
                Integer.toString(task.getStatus()), Integer.toString(task.getRepUser().getCode()));
    }
}