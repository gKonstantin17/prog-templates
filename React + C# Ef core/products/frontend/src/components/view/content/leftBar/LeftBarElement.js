function LeftBarElement({title,isActive, onClick }) {
    return(<button className="left-tab" onClick={onClick}>
        {title}
    </button>)
}
export {LeftBarElement}