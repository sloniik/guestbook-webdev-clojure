(ns guestbook.routes.auth
  (:import java.text.SimpleDateFormat
           java.util.Date)
  (:require [compojure.core :refer [defroutes GET POST]]
            [guestbook.views.layout :as layout]
            [hiccup.form :refer
             [form-to label text-field password-field submit-button]]
            [noir.response :refer [redirect]]
            [noir.session :as session]
            [noir.validation
             :refer [rule errors? has-value? on-error]]
            [noir.util.crypt :as crypt]
            [guestbook.models.db :as db]))

(defn now [] (.format (SimpleDateFormat. "yyyy.MM.dd HH:mm:ss") (Date.)))

(defn format-error [[error]]
  [:p.error error])

(defn control [field name text]
  (list (on-error name format-error)
        (label name text)
        (field name)
        [:br]))

(defn registration-page []
  (layout/common
    (form-to [:post "/register"]
             (control text-field :id "screen name")
             (control password-field :pass "Password")
             (control password-field :pass1 "Retype Password")
             (submit-button "Create Account"))))

(defn login-page []
  (layout/common
    (form-to [:post "/login"]
             (control text-field :id "screen name")
             (control password-field :pass "Password")
             (submit-button "login"))))

(defn logout-page
  []
  (layout/common
    (form-to
      [:post "/logout"]
      [:h1 "Guestbook" (session/get :user)]
      (submit-button "Logout"))))

;; rules for authentification
(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (rule (has-value? id)
          [:id "screen name is required"])
    (rule (has-value? pass)
          [:pass "password is required"])
    (rule (and user (crypt/compare pass (:password_hash user)))
          [:pass "invalid password"])
    (if (errors? :id :pass)
      (login-page)
      (do
        (session/put! :user id)
        (redirect "/")))))

(defn handle-registration [id pass pass1]
  (rule (= pass pass1)
        [:pass "password was not retyped correctly"])
  (if (errors? :pass)
    (registration-page)
    (do
      (db/add-user-record {:user_name     id
                           :email         (str id "@" id ".ru")
                           :password_hash (crypt/encrypt pass)
                           :salt          id
                           :dt_created    (now)
                           :is_active     false   ;при создании человек не активен, так как надо подтвердить email
                           :is_banned     false
                           :is_online     true
                           :is_admin      false})
      (redirect "/login"))))

(defroutes auth-routes
           (GET "/register" [] (registration-page))
           (POST "/register" [id pass pass1]
             (handle-registration id pass pass1))

           (GET "/login" [] (login-page))
           (POST "/login" [id pass]
             (handle-login id pass))

           (GET "/logout" []
             (layout/common
               (form-to [:post "/logout"]
                        (submit-button "logout"))))
           (POST "/logout" []
             (session/clear!)
             (redirect "/")))