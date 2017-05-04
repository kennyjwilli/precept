(ns ^:figwheel-always libx.todomvc.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [goog.events :as events]
            [libx.state :refer [state store]]
            [libx.core :refer [start! then]]
            [libx.spec.sub :as sub]
            [libx.todomvc.views]
            [libx.todomvc.schema :refer [app-schema]]
            [libx.todomvc.rules :refer [app-session]]
            [reagent.core :as reagent]
            [secretary.core :as secretary])
  (:import [goog History]
           [goog.history EventType]))

(enable-console-print!)

;; Instead of secretary consider:
;;   - https://github.com/DomKM/silk
;;   - https://github.com/juxt/bidi
(defroute "/" [] (then :ui/set-visibility-filter-action
                       {:ui/visibility-filter :all}))

(defroute "/:filter" [filter] (then :ui/set-visibility-filter-action
                                    {:ui/visibility-filter (keyword filter)}))

(def history
  (doto (History.)
    (events/listen EventType.NAVIGATE
                   (fn [event] (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn mount-components []
  (reagent/render [libx.todomvc.views/todo-app] (.getElementById js/document "app"))
  (.addEventListener js/window "mousedown" #(then :mouse/down-action {:event %}))
  (.addEventListener js/window "mousemove" #(then :mouse/move-action {:event %}))
  (.addEventListener js/window "mouseup" #(then :mouse/up-action {:event %})))

(defn todo [title]
  (let [id (random-uuid)]
    [[id :todo/title title]
     [id :todo/done false]
     [id :dom/draggable? :tag]]))

(def facts (into (todo "Hi") (todo "there!")))

(defn ^:export main []
    (start! {:session app-session :schema app-schema :facts facts})
    (mount-components))
