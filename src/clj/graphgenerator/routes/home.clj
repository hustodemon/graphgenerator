(ns graphgenerator.routes.home
  (:require
   [graphgenerator.generator.core :as generator]
   [graphgenerator.layout :as layout]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [graphgenerator.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   ))


(defn home-page [request]
  (layout/render request "home.html"))


(defn home-routes []
  [""
   {:middleware [;; middleware/wrap-csrf. todo revive
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/generate-dot"
    {:post (fn [req]
             (try
               (response/ok
                (generator/generate-graph-dot
                 (:body-params req)))
               (catch Throwable e
                 (response/internal-server-error (-> e .getData :msg)))))}]
   ["/generate"
    {:post (fn [req]
             (try
               (response/ok
                (generator/generate-graph-rhizome
                 (edn/read-string
                  (:body-params req))
                 ));; todo catch only exception-info
               (catch Throwable e
                 ;; todo ISE - something else
                 (response/internal-server-error (str e)))))}]
   ["/docs"
    {:get (fn [_]
            (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                (response/header "Content-Type" "text/plain; charset=utf-8")))}]])

