import {Specification} from "./content/spec/Specification";
import {Order} from "./content/order/Order";
import {Warehouse} from "./content/warehouse/Warehouse";


function MainContent({activeTab}) {
    const content = {
        specification: <Specification />,
        order: <Order />,
        warehouse: <Warehouse />
    };
    return (<div className="content">
        {content[activeTab]}
    </div>)
}
export {MainContent}