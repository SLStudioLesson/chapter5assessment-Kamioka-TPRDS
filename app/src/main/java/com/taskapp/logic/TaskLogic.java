package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        //findAllメソッドを用いてtaskListを作成する
        //taskList内の全てのTaskからfor文を用いて出力する
        //loginUserとtaskListのrepUserが合致した場合はあなたが担当していますと出力する
        //statusの値に応じて出力する文字列を変更する
        List<Task> taskList = taskDataAccess.findAll();
        for (Task task : taskList) {
            System.out.print(task.getCode() + ". タスク名：" + task.getName());
            
            if (loginUser.equals(task.getRepUser())) {
                System.out.print(", 担当者名：あなたが担当しています");
            } else {
                System.out.print(", 担当者名：" + task.getRepUser().getName() + "が担当しています");
            }

            String status = "未着手";
            if (task.getStatus() == 1) status = "着手中";
            if (task.getStatus() == 2) status = "完了";
            System.out.println(", ステータス：" + status);
        }
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {
        if (userDataAccess.findByCode(repUserCode) == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }
        
        taskDataAccess.save(new Task(code, name, 0, userDataAccess.findByCode(repUserCode)));
        logDataAccess.save(new Log(code, loginUser.getCode(), 0, LocalDate.now()));
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status,
                            User loginUser) throws AppException {
        //ユーザー情報が存在するかとステータスのバリデーションを行います
        //taskDataAccessとlogDataAccessにそれぞれのインスタンスを生成して渡します
        if (taskDataAccess.findByCode(code) == null) throw new AppException("存在するタスクコードを入力してください");
        if (!(status - taskDataAccess.findByCode(code).getStatus() == 1)) throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
        
        String name = taskDataAccess.findByCode(code).getName();
        User user = taskDataAccess.findByCode(code).getRepUser();
        taskDataAccess.update(new Task(code, name, status, user));
        logDataAccess.save(new Log(code, loginUser.getCode(), status, LocalDate.now()));
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    public void delete(int code) throws AppException {
        //ユーザー情報が存在するかとステータスのバリデーションを行います
        //taskDataAccessとlogDataAccessにそれぞれのインスタンスを生成して渡します
        if (taskDataAccess.findByCode(code) == null) throw new AppException("存在するタスクコードを入力してください");
        if (!(taskDataAccess.findByCode(code).getStatus() == 2)) throw new AppException("ステータスが完了のタスクを選択してください");

        String deleteTask = taskDataAccess.findByCode(code).getName();
        taskDataAccess.delete(code);
        logDataAccess.deleteByTaskCode(code);

        System.out.println(deleteTask + "の削除が完了しました。");
    }
}