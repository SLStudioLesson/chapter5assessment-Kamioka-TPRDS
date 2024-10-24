package com.taskapp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.taskapp.exception.AppException;
import com.taskapp.logic.TaskLogic;
import com.taskapp.logic.UserLogic;
import com.taskapp.model.User;

public class TaskUI {
    private final BufferedReader reader;

    private final UserLogic userLogic;

    private final TaskLogic taskLogic;

    private User loginUser;

    public TaskUI() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        userLogic = new UserLogic();
        taskLogic = new TaskLogic();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param reader
     * @param userLogic
     * @param taskLogic
     */
    public TaskUI(BufferedReader reader, UserLogic userLogic, TaskLogic taskLogic) {
        this.reader = reader;
        this.userLogic = userLogic;
        this.taskLogic = taskLogic;
    }

    /**
     * メニューを表示し、ユーザーの入力に基づいてアクションを実行します。
     *
     * @see #inputLogin()
     * @see com.taskapp.logic.TaskLogic#showAll(User)
     * @see #selectSubMenu()
     * @see #inputNewInformation()
     */
    public void displayMenu() {
        System.out.println("タスク管理アプリケーションにようこそ!!");

        //ログインメニュー
        inputLogin();

        // メインメニュー
        boolean flg = true;
        while (flg) {
            try {
                System.out.println("以下1~3のメニューから好きな選択肢を選んでください。");
                System.out.println("1. タスク一覧, 2. タスク新規登録, 3. ログアウト");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();

                System.out.println();

                switch (selectMenu) {
                    case "1":
                        taskLogic.showAll(this.loginUser);
                        selectSubMenu();
                        break;
                    case "2":
                        inputNewInformation();
                        break;
                    case "3":
                        System.out.println("ログアウトしました。");
                        flg = false;
                        break;
                    default:
                        System.out.println("選択肢が誤っています。1~3の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからのログイン情報を受け取り、ログイン処理を行います。
     *
     * @see com.taskapp.logic.UserLogic#login(String, String)
     */
    public void inputLogin() {
        //ユーザーからメールアドレスとパスワードの入力を文字列で受け取る
        //受け取った入力をuserLogicに渡し、帰ってきたUserクラスの名前を出力する
        while (true) {
            try {
                System.out.print("メールアドレスを入力してください：");
                String inputEmail = reader.readLine();
                System.out.print("パスワードを入力してください：");
                String inputPassword = reader.readLine();

                this.loginUser = userLogic.login(inputEmail, inputPassword);
                System.out.println("ユーザー名：" + this.loginUser.getName() + "でログインしました。\n");
                break;
            } catch (AppException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ユーザーからの新規タスク情報を受け取り、新規タスクを登録します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#save(int, String, int, User)
     */
    public void inputNewInformation() {
        //ユーザーからタスクコード・タスク名・担当するユーザーコードを受け取り、バリデーションを行います。
        //TaskLogicにそれらの値を渡して処理を行います。
        while (true) {
            try {
                //タスクコードバリデーション
                System.out.print("タスクコードを入力してください：");
                String inputTaskCode = reader.readLine();

                if (!isNumeric(inputTaskCode)) {
                    throw new AppException("コードは半角の数字で入力してください");
                }
                int taskCode = Integer.parseInt(inputTaskCode);

                //タスク名バリデーション
                System.out.print("タスク名を入力してください：");
                String inputTaskName = reader.readLine();

                if (inputTaskName.length() > 10) {
                    throw new AppException("タスク名は10文字以内で入力してください");
                }
                //ユーザーコードバリデーション
                System.out.print("担当するユーザーのコードを選択してください：");
                String inputTaskRepUser = reader.readLine();

                if (!isNumeric(inputTaskRepUser)) {
                    throw new AppException("ユーザーのコードは半角の数字で入力してください");
                }
                int taskRepUser = Integer.parseInt(inputTaskRepUser);

                taskLogic.save(taskCode, inputTaskName, taskRepUser, this.loginUser);

                System.out.println(inputTaskName + "の登録が完了しました。");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage() + "\n");
            }
        }
    }

    /**
     * タスクのステータス変更または削除を選択するサブメニューを表示します。
     *
     * @see #inputChangeInformation()
     * @see #inputDeleteInformation()
     */
    public void selectSubMenu() {
        //ユーザーから入力値を受け取り、1だとinputChangeInformation、2だとinputDeleteInformation、3だと前の画面に遷移する
        while (true) {
            try {
                System.out.println("1. タスクのステータス変更, 2. タスク削除, 3. メインメニューに戻る");
                System.out.print("選択肢：");
                String inputCode = reader.readLine();
                switch (inputCode) {
                    case "1":
                        inputChangeInformation();
                        break;
                    case "2":
                        inputDeleteInformation();
                        break;
                    case "3":
                        return;
                    default:
                        System.out.println("1または2を入力してください");
                        continue;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ユーザーからのタスクステータス変更情報を受け取り、タスクのステータスを変更します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#changeStatus(int, int, User)
     */
    public void inputChangeInformation() {
        //ユーザーから変更するタスクコード・変更するステータスを受け取り、バリデーションを行います。
        //TaskLogicにそれらの値を渡して変更処理を行います。
        while (true) {
            try {
                System.out.print("ステータスを変更するタスクコードを入力してください：");
                String inputTaskCode = reader.readLine();
                if(!isNumeric(inputTaskCode)) throw new AppException("コードは半角の数字で入力してください");
                int taskCode = Integer.parseInt(inputTaskCode);

                System.out.print("どのステータスに変更するか選択してください。\n1. 着手中, 2. 完了\n選択肢：\n");
                String inputStatus = reader.readLine();
                if(!isNumeric(inputStatus)) throw new AppException("ステータスは半角の数字で入力してください");
                int status = Integer.parseInt(inputStatus);
                if(!(status == 1 || status == 2)) throw new AppException("ステータスは1・2の中から選択してください");

                taskLogic.changeStatus(taskCode, status, loginUser);
                System.out.println("ステータスの変更が完了しました。\n");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage() + "\n");
            }
        }
    }

    /**
     * ユーザーからのタスク削除情報を受け取り、タスクを削除します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#delete(int)
     */
    public void inputDeleteInformation() {
        //ユーザーから削除するタスクコードを受け取り、バリデーションを行います。
        //TaskLogicにそれらの値を渡して削除処理を行います。
        while (true) {
            try {
                System.out.print("削除するタスクコードを入力してください：");
                String inputTaskCode = reader.readLine();
                if(!isNumeric(inputTaskCode)) throw new AppException("コードは半角の数字で入力してください");
                int taskCode = Integer.parseInt(inputTaskCode);

                taskLogic.delete(taskCode);
                System.out.println("の削除が完了しました。\n");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage() + "\n");
            }
        }
    }

    /**
     * 指定された文字列が数値であるかどうかを判定します。
     * 負の数は判定対象外とする。
     *
     * @param inputText 判定する文字列
     * @return 数値であればtrue、そうでなければfalse
     */
    public boolean isNumeric(String inputText) {
        //streamを用いて数値か判定する
        if (inputText.chars().allMatch(c -> Character.isDigit((char) c))) {
            return true;
        } else {
            return false;
        }
    }
}