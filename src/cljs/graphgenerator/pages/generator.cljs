(ns graphgenerator.pages.generator
  "
  The UI related to the generator
  "
  (:require
    [day8.re-frame.http-fx]
    [re-frame.core :as rf]
    [re-com.core :as rc]
    [graphgenerator.events]))


(defn- text-area []
  (let [text (rf/subscribe [:generator/input])]
    (fn []
      [rc/input-textarea
       :model text
       :on-change (fn [val]
                    (rf/dispatch [:common/set-value val :generator/input]))
       :width "800px"
       :height "400px"
       :style {:font-family "Consolas, Lucida Console, monospace"}])))


(defn- presets []
  (let [graph-type      (rf/subscribe [:generator/selected-graph-type])
        presets         (rf/subscribe [:generator/presets])
        selected-preset (rf/subscribe [:generator/selected-preset])]
    (fn []
      (let [choices (cons {:id 0 :label "<nothing>" :text "nothing"}
                          (get @presets @graph-type))]
        [rc/single-dropdown
         :choices   choices
         :model     (second @selected-preset)
         :width     "200px"
         :on-change (fn [val]
                      (rf/dispatch
                       [:generator/select-preset
                        [@graph-type val]]))]))))


(defn- selection []
  (let [graph-types (rf/subscribe [:generator/graph-types])
        graph-type (rf/subscribe [:generator/selected-graph-type])]
    (fn []
      [rc/single-dropdown
       :choices @graph-types
       :model graph-type
       :width "200px"
       :on-change (fn [val] ;; todo a single dispatch
                    (rf/dispatch [:common/set-value
                                  [val 0]
                                  :generator/selected-preset])
                    (rf/dispatch [:common/set-value
                                  val
                                  :generator/selected-graph-type]))])))


(defn gen-page []
  (let [graph (rf/subscribe [:graph])]
    (fn []
      [:section.section>div.container>div.content
       [rc/v-box
        :gap "20px"
        :children
        [[rc/h-box
          :gap "10px"
          :children
          [[text-area]
           [rc/v-box
            :gap "10px"
            :children
            [[selection]
             [presets]
             [rc/button
              :label "Generate!"
              :on-click #(rf/dispatch [:generate])
              :class "btn-primary"
              :style {:width "200px"}]
             ]]
           ]]
         [:div
          {:dangerouslySetInnerHTML {:__html @graph}
           :style                   {:text-align "center"}}] ]]])))
