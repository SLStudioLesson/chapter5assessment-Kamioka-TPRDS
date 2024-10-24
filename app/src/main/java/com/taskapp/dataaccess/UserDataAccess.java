package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.taskapp.model.User;

public class UserDataAccess {
    private final String filePath;

    public UserDataAccess() {
        filePath = "app/src/main/resources/users.csv";
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     */
    public UserDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * メールアドレスとパスワードを基にユーザーデータを探します。
     * @param email メールアドレス
     * @param password パスワード
     * @return 見つかったユーザー
     */
    public User findByEmailAndPassword(String email, String password) {
        //users.csvを1行ずつ読み込み、emailとpasswordが合致した場合にUserインスタンスを作成しreturnする
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] lineData = line.split(",");
                if (lineData[2].equals(email) && lineData[3].equals(password)) {
                    User user = new User(Integer.parseInt(lineData[0]), lineData[1], lineData[2], lineData[3]);
                    return user;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return null;
    }

    /**
     * コードを基にユーザーデータを取得します。
     * @param code 取得するユーザーのコード
     * @return 見つかったユーザー
     */
    public User findByCode(int code) {
        //users.csvを1行ずつ読み込み、codeが合致した場合にUserインスタンスを作成しreturnする
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] lineData = line.split(",");
                int userCode = Integer.parseInt(lineData[0]);
                if (userCode == code) {
                    User user = new User(userCode, lineData[1], lineData[2], lineData[3]);
                    return user;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return null;
    }
}
