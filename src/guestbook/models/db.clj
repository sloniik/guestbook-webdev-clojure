;; There are function which are for working with database
;; low-level functions

(ns guestbook.models.db
  (:gen-class)
  (:import java.text.SimpleDateFormat
           java.util.Date
           com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [clojure.java.jdbc :as jdbc]
            [jdbc.pool.c3p0 :as pool]
            [korma.core :as k]
            [noir.util.crypt :as crypt]
            [korma.db :as kdb]))

(defn now [] (.format (SimpleDateFormat. "yyyy.MM.dd HH:mm:ss") (Date.)))

(def db (kdb/mysql
          {:classname   "com.mysql.jdbc.Driver"
           :subprotocol "mysql"
           :ssl?        false
           :subname     "//127.0.0.1:3306/dnk_test"
           :user        "root"
           :password    "12345"
           :make-pool?  true}))
(kdb/defdb korma-db db)

(declare guestbook)
(k/defentity guestbook
             (k/entity-fields
               :id :timestamp :name :message))

;; Create the guestbook
(defn create-guestbook-table []
  (k/exec-raw ["DROP TABLE IF EXISTS guestbook;"])
  (k/exec-raw ["CREATE TABLE guestbook
             (
              id bigint not null auto_increment
              ,timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
              ,name VARCHAR(255)
              ,message VARCHAR(255)
              ,primary key (id)
              )
              engine=innodb;"])
  (k/exec-raw ["CREATE INDEX timestamp_index ON guestbook (timestamp)"]))


(defn read-guests
  []
  (k/select :guestbook
            (k/order :timestamp :desc)))

(defn add-user-record
  ;{:user_name     login
  ; :email         email
  ; :password_hash passwd
  ; :salt          salt
  ; :dt_created    (u/now)
  ; :is_active     false   ;при создании человек не активен, так как надо подтвердить email
  ; :is_banned     false
  ; :is_admin      false
  ; :email_code    code}
  [user-map]
  (println "creating user:" user-map)
  (let [result (k/insert :users
            (k/values user-map))]
    (println result)
    result))

(defn get-user
  [id]
  (println "getting user: " id)
  (let [result (k/select :users
            (k/where {:user_name id}))]
    (println result)
    (last(into [] result))))

(defn save-message
  [name message]
  (k/insert :guestbook
    (k/values {:name name
               :message message
               :timestamp (new java.util.Date)})))

;(add-user-record {:user_name     "serge"
;                  :email         (str "serge@mail.ru" )
;                  :password_hash (crypt/encrypt "12345")
;                  :salt          "sergey"
;                  :dt_created    (now)
;                  :is_active     false   ;при создании человек не активен, так как надо подтвердить email
;                  :is_banned     false
;                  :is_online     true
;                  :is_admin      false})