(ns graphgenerator.routes.home
  (:require
   [graphgenerator.layout :as layout]
   [clojure.java.io :as io]
   [graphgenerator.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))


(defn home-page [request]
  (layout/render request "home.html"))


(defn make-docs-handler [path]
  (fn [_]
    (-> (response/ok (-> path io/resource slurp))
        (response/header "Content-Type" "text/plain; charset=utf-8"))))


(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/docs" {:get (make-docs-handler "docs/docs.md")}]
   ["/api-docs" {:get (make-docs-handler "docs/api.md")}]])
