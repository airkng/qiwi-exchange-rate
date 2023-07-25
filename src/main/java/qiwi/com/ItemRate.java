package qiwi.com;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@XmlRootElement(name = "Valuta")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemRate {

    @XmlElement(name = "Item")
    private List<Item> itemsList;
}
class Item {
    @XmlElement(name = "ID")
    String id;
    @XmlAttribute(name = "Name")
    String rusName;
    @XmlAttribute(name = "EngName")
    String engName;
}
