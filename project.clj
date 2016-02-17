(defproject guestbook "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.8.0-RC4"]
                 [org.clojure/data.generators "0.1.2"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.2"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [ring-server "0.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring-json-params "0.1.3"]
                 [clj-json "0.5.3"]
                 [mysql/mysql-connector-java "5.1.38"]
                 [korma "0.4.2"]
                 [lib-noir "0.7.6"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler guestbook.handler/app
         :init guestbook.handler/init
         :destroy guestbook.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.4.0"]]}})
