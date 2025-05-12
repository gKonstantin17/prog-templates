import {BarElement} from "./navBar/BarElement";

function NavBar({activeTab,setActiveTab}) {
    const tabs = {
        spec: "specification",
        order: 'order',
        wh: 'warehouse'
    }
    return (<div className="nav-bar">
        <BarElement title="Спецификация"
                    key={tabs.spec}
                    isActive={activeTab === tabs.spec}
                    onClick={() => setActiveTab(tabs.spec)} />
        <BarElement title="Заказ"
                    key={tabs.order}
                    isActive={activeTab === tabs.order}
                    onClick={() => setActiveTab(tabs.order)}/>
        <BarElement title="Склад"
                    key={tabs.wh}
                    isActive={activeTab === tabs.wh}
                    onClick={() => setActiveTab(tabs.wh)}/>
    </div>)
}
export {NavBar}

