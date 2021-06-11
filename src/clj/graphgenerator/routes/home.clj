(ns graphgenerator.routes.home
  (:require
   [graphgenerator.layout :as layout]
   [clojure.java.io :as io]
   [graphgenerator.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))


(defn home-page [request]
  (layout/render request "home.html"))


(defn docs [_]
  (-> (response/ok (-> "docs/docs.md" io/resource slurp))
      (response/header "Content-Type" "text/plain; charset=utf-8")))


(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/docs" {:get docs}]])
