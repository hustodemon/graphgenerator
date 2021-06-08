(defproject graphgenerator "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[rhizome  "0.2.9"]
                 [cljsjs/ace "1.4.2-0"]
                 [re-com "2.13.2"]
                 
                 ;; luminus shit below
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [cljs-ajax "0.8.3"]
                 [clojure.java-time "0.3.2"]
                 [com.cognitect/transit-clj "1.0.324"]
                 [cprop "0.1.17"]
                 [day8.re-frame/http-fx "0.2.3"]
                 [expound "0.8.9"]
                 [funcool/struct "1.4.0"]
                 [json-html "0.4.7"]
                 [luminus-transit "0.1.2"]
                 [luminus-undertow "0.1.11"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.10.5"]
                 [metosin/muuntaja "0.6.8"]
                 [metosin/reitit "0.5.13"]
                 [metosin/ring-http-response "0.9.2"]
                 [mount "0.1.16"]
                 [nrepl "0.8.3"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.10.866" :scope "provided"]
                 [org.clojure/tools.cli "1.0.206"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.webjars.npm/bulma "0.9.2"]
                 [org.webjars.npm/material-icons "0.3.1"]
                 [org.webjars/webjars-locator "0.40"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [re-frame "1.2.0"]
                 [reagent "1.0.0"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.9.3"]
                 [ring/ring-defaults "0.3.2"]
                 [selmer "1.12.40"]]

  :min-lein-version "2.0.0"
  
  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot graphgenerator.core

  :plugins [[lein-cljsbuild "1.1.7"]] 
  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :server-logfile "log/figwheel-logfile.log"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
  

  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :cljsbuild{:builds
              {:min
               {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                :compiler
                {:output-dir "target/cljsbuild/public/js"
                 :output-to "target/cljsbuild/public/js/app.js"
                 :source-map "target/cljsbuild/public/js/app.js.map"
                 :optimizations :advanced
                 :pretty-print false
                 :infer-externs true
                 :closure-warnings
                 {:externs-validation :off :non-standard-jsdoc :off}
                 :externs ["react/externs/react.js"]}}}}
             
             :aot :all
             :uberjar-name "graphgenerator.jar"
             :source-paths ["env/prod/clj" ]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn" ]
                  :dependencies [[binaryage/devtools "1.0.3"]
                                 [cider/piggieback "0.5.2"]
                                 [doo "0.1.11"]
                                 [figwheel-sidecar "0.5.20"]
                                 [pjstadig/humane-test-output "0.11.0"]
                                 [prone "2021-04-23"]
                                 [re-frisk "1.5.1"]
                                 [ring/ring-devel "1.9.3"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [jonase/eastwood "0.3.5"]
                                 [lein-doo "0.1.11"]
                                 [lein-figwheel "0.5.20"]] 
                  :cljsbuild{:builds
                   {:app
                    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :figwheel {:on-jsload "graphgenerator.core/mount-components"}
                     :compiler
                     {:output-dir "target/cljsbuild/public/js/out"
                      :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                      :optimizations :none
                      :preloads [re-frisk.preload]
                      :output-to "target/cljsbuild/public/js/app.js"
                      :asset-path "/js/out"
                      :source-map true
                      :main "graphgenerator.app"
                      :pretty-print true}}}}
                  
                  
                  :doo {:build "test"}
                  :source-paths ["env/dev/clj" ]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :timeout 120000}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn" ]
                  :resource-paths ["env/test/resources"] 
                  :cljsbuild 
                  {:builds
                   {:test
                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to "target/test.js"
                      :main "graphgenerator.doo-runner"
                      :optimizations :whitespace
                      :pretty-print true}}}}
                  
                  }
   :profiles/dev {}
   :profiles/test {}})
