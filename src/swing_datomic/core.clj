(ns swing-datomic.core
  (:require [datomic.client.api :as d])
  (:import [javax.swing JFrame JLabel JPanel JButton JTextField])
  (:gen-class))

(defmacro on-action
  [component event & body]
  `(. ~component addActionListener
      (proxy [java.awt.event.ActionListener] []
        (actionPerformed [~event] ~@body))))

(defn -main
  []
  (let [id (JLabel. "ID Film")
        field (JTextField. "" 2)
        title (JLabel. "")
        button (doto (JButton. "Ambil Judul")
                 (on-action event
                            (.setText title
                                      (-> (d/q '[:find ?title
                                                 :in $ ?id
                                                 :where
                                                 [?e :movie/id ?id]
                                                 [?e :movie/title ?title]]
                                               (d/db (d/connect (d/client {:server-type :datomic-local
                                                                           :system "db"})
                                                                {:db-name "swing-datomic"}))
                                               (-> (.getText field)
                                                   Integer/parseInt))
                                          ffirst))))
        panel (doto (JPanel.)
                (.setOpaque true)
                (.add id)
                (.add field)
                (.add button)
                (.add title))]
    (doto (JFrame. "Swing + Datomic")
      (.setContentPane panel)
      (.setSize 300 100)
      (.setVisible true))))
